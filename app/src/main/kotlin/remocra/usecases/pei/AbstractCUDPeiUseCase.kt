package remocra.usecases.pei

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.authn.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiData
import remocra.data.PeiForCalculDispoData
import remocra.data.PeiForNumerotationData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.TypeSourceModification
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.eventbus.EventBus
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecases.AbstractCUDUseCase
import remocra.web.pei.CalculDispoUseCase
import remocra.web.pei.NumerotationUseCase
import java.time.Clock
import java.time.ZonedDateTime

/**
 * Classe mère des useCases des opérations C, U, D des PEI.
 * Permet de gérer les opérations transverses, calcul de la numérotation, de la dispo, et déclenchement des events, communes aux différents types d'opérations
 * Dans le cadre d'une insertion, on ne peut en aucun cas renseigner ses visites. Le PEI sera donc mis en indisponible.
 * Si un jour, on ajoute la saisie de visites dans la création d'un PEI, il faudra mettre à jour sa disponibilité.
 */
abstract class AbstractCUDPeiUseCase(private val typeOperation: TypeOperation) : AbstractCUDUseCase<PeiData>() {
    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var calculNumerotationUseCase: NumerotationUseCase

    @Inject
    lateinit var calculDispoUseCase: CalculDispoUseCase

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var clock: Clock

    override fun postEvent(element: PeiData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.idUtilisateur, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
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

    override fun execute(element: PeiData): PeiData {
        if (typeOperation != TypeOperation.DELETE) {
            // Si on est en création OU si on autorise la renumérotation, et qu'elle est nécessaire
            if (element.peiNumeroInterne == null || element.peiNumeroComplet == null ||
                parametresProvider.getParametreBoolean(GlobalConstants.PARAM_PEI_RENUMEROTATION_INTERNE_AUTO) == true &&
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
                    )

                    elementConcret.peiDisponibiliteTerrestre = calculDispoUseCase.execute(peiForCalculDispoData)
                }
            }
        }

        // Tout est à jour, on peut enregistrer l'élément :
        executeSpecific(element)

        // On rend la main au parent pour la logique d'événements
        return element
    }

    /**
     * Méthode permettant de décrire tout ce qui est spécifique à chaque opération, typiquement le service métier à appeler
     */
    abstract fun executeSpecific(element: PeiData): Any?
}
