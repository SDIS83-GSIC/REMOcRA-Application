package remocra.usecase.pei

import com.google.inject.Inject
import com.google.inject.Provider
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiData
import remocra.data.PeiForCalculDispoData
import remocra.data.PeiForNumerotationData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

/**
 * Classe mère des useCases des opérations C, U, D des PEI.
 * Permet de gérer les opérations transverses, calcul de la numérotation, de la dispo, et déclenchement des events, communes aux différents types d'opérations
 * Dans le cadre d'une insertion, on ne peut en aucun cas renseigner ses visites. Le PEI sera donc mis en indisponible.
 * Si un jour, on ajoute la saisie de visites dans la création d'un PEI, il faudra mettre à jour sa disponibilité.
 */
abstract class AbstractCUDPeiUseCase(typeOperation: TypeOperation) : AbstractCUDUseCase<PeiData>(typeOperation) {
    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var calculNumerotationUseCase: NumerotationUseCase

    @Inject
    lateinit var calculDispoUseCase: CalculDispoUseCase

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var parametresProvider: Provider<ParametresProvider>

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var pibiRepository: PibiRepository

    @Inject lateinit var penaRepository: PenaRepository

    override fun postEvent(element: PeiData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
        eventBus.post(PeiModifiedEvent(element.peiId))
    }

    /**
     * Fonction permettant de savoir s'il faut recalculer le numéro interne du PEI car un de ses attributs structurants a été modifié. <br />
     *
     * Cela ne veut pas dire que le numéro interne sera différent, c'est le calcul qui le déterminera.
     */
    private fun needComputeNumeroInterne(element: PeiData): Boolean {
        return calculNumerotationUseCase.needComputeNumeroInterneCommune(element.peiCommuneId, element.peiCommuneIdInitial, element.peiZoneSpecialeId, element.peiZoneSpecialeIdInitial) ||
            calculNumerotationUseCase.needComputeNumeroInterneNatureDeci(element.peiNatureDeciId, element.peiNatureDeciIdInitial) ||
            calculNumerotationUseCase.needComputeNumeroInterneDomaine(element.peiDomaineId, element.peiDomaineIdInitial)
    }

    override fun execute(userInfo: UserInfo?, element: PeiData): PeiData {
        if (typeOperation != TypeOperation.DELETE) {
            // Si on est en création OU si on autorise la renumérotation, et qu'elle est nécessaire
            if (element.peiNumeroInterne == null || element.peiNumeroComplet == null ||
                parametresProvider.get().getParametreBoolean(GlobalConstants.PARAM_PEI_RENUMEROTATION_INTERNE_AUTO) == true &&
                needComputeNumeroInterne(element)
            ) {
                // Création de l'objet data pour le calcul
                val peiForNumerotationData = PeiForNumerotationData(
                    peiNumeroInterne = null,
                    peiId = element.peiId,
                    peiCommuneId = element.peiCommuneId,
                    peiZoneSpecialeId = element.peiZoneSpecialeId,
                    domaine = dataCacheProvider.getDomaines().values.firstOrNull { it.domaineId == element.peiDomaineId },
                    nature = dataCacheProvider.getNatures().values.firstOrNull { it.natureId == element.peiNatureId },
                    natureDeci = dataCacheProvider.getNaturesDeci().values.firstOrNull { it.natureDeciId == element.peiNatureDeciId },
                )

                // Calcul du numéro *interne*
                val numeroInterne = calculNumerotationUseCase.computeNumeroInterne(peiForNumerotationData)
                peiForNumerotationData.peiNumeroInterne = numeroInterne

                // Calcul du numéro *complet*, avec un numéro interne mis à jour
                val numeroComplet = calculNumerotationUseCase.computeNumero(peiForNumerotationData)

                element.peiNumeroInterne = numeroInterne
                element.peiNumeroComplet = numeroComplet
            }

            // Si c'est une insertion, on met directement le PEI indisponible
            // (Il n'est pas encore présent en base et n'a pas de visites)
            if (typeOperation == TypeOperation.INSERT) {
                element.peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE
            } else {
                // à partir de là, on a besoin de travailler sur le type concret
                if (TypePei.PIBI == element.peiTypePei) {
                    val elementConcret = element as PibiData

                    val lastVisite = visiteRepository.getLastVisiteDebitPression(element.peiId)

                    // Calcul de la dispo du PEI
                    val peiForCalculDispoData = PeiForCalculDispoData(
                        peiId = elementConcret.peiId,
                        peiNatureId = elementConcret.peiNatureId,
                        diametreId = elementConcret.pibiDiametreId,
                        debit = lastVisite?.visiteCtrlDebitPressionDebit,
                        pression = lastVisite?.visiteCtrlDebitPressionPression?.toDouble(),
                        pressionDynamique = lastVisite?.visiteCtrlDebitPressionPressionDyn?.toDouble(),
                        penaCapacite = null,
                        penaCapaciteIllimitee = null,
                        penaCapaciteIncertaine = null,
                    )

                    elementConcret.peiDisponibiliteTerrestre = calculDispoUseCase.execute(peiForCalculDispoData)
                } else {
                    val elementConcret = element as PenaData

                    // Calcul de la dispo du PEI
                    val peiForCalculDispoData = PeiForCalculDispoData(
                        peiId = elementConcret.peiId,
                        peiNatureId = elementConcret.peiNatureId,
                        diametreId = null,
                        debit = null,
                        pression = null,
                        pressionDynamique = null,
                        penaCapacite = elementConcret.penaCapacite,
                        penaCapaciteIllimitee = elementConcret.penaCapaciteIllimitee,
                        penaCapaciteIncertaine = elementConcret.penaCapaciteIncertaine,
                    )

                    elementConcret.peiDisponibiliteTerrestre = calculDispoUseCase.execute(peiForCalculDispoData)
                }
            }
        }

        // Tout est à jour, on peut enregistrer l'élément :
        executeSpecific(userInfo, element)

        // On rend la main au parent pour la logique d'événements
        return element
    }

    protected fun upsertPei(peiData: PeiData) {
        // On insert le PEI
        peiRepository.upsert(peiData)

        // Puis on insert le PENA / PIBI
        if (peiData is PibiData) {
            pibiRepository.upsertPibi(peiData)

            // Si le Bi est jumelé à un autre, il faut mettre à jour l'autre
            if (peiData.pibiJumeleId != null) {
                pibiRepository.updateJumelage(peiData.peiId, peiData.pibiJumeleId!!)
            } else {
                //  si aucun jumelage on enlève les potentiels lien avec ce pei
                pibiRepository.removeJumelage(peiData.peiId)
            }
        }

        if (peiData is PenaData) {
            penaRepository.upsertPena(peiData)
        }
    }

    /**
     * Méthode permettant de décrire tout ce qui est spécifique à chaque opération, typiquement le service métier à appeler
     */
    protected abstract fun executeSpecific(userInfo: UserInfo?, element: PeiData): Any?

    override fun checkContraintes(userInfo: UserInfo?, element: PeiData) {
        val isInZoneCompetence = peiRepository.isInZoneCompetence(
            srid = appSettings.sridInt,
            coordonneeY = element.coordonneeY,
            coordonneeX = element.coordonneeX,
            idOrganisme = userInfo?.organismeId ?: throw RemocraResponseException(ErrorType.FORBIDDEN),
        )
        if (!isInZoneCompetence) {
            throw RemocraResponseException(ErrorType.FORBIDDEN_ZONE_COMPETENCE)
        }

        val isSaisieLibreEnabled = parametresProvider.get().getParametreBoolean(GlobalConstants.VOIE_SAISIE_LIBRE)!!
        // Normalement impossible, sauf sur changement du paramètre sans nettoyage
        if (!isSaisieLibreEnabled && element.peiVoieTexte != null) {
            throw RemocraResponseException(ErrorType.PEI_VOIE_SAISIE_LIBRE_FORBIDDEN)
        }

        // On veut obligatoirement l'un ou l'autre des champs
        if (element.peiVoieTexte == null && element.peiVoieId == null) {
            throw RemocraResponseException(ErrorType.PEI_VOIE_OBLIGATOIRE)
        }

        // On ne veut pas les 2 champs en même temps (XOR non nullable)
        if (!element.peiVoieTexte.isNullOrBlank() && element.peiVoieId != null) {
            throw RemocraResponseException(ErrorType.PEI_VOIE_XOR)
        }
    }
}
