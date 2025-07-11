package remocra.usecase.pei

import com.google.inject.Inject
import com.google.inject.Provider
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.zoneintegration.ComputeZoneSpecialeUseCase

/**
 * Classe mère des useCases des opérations C, U, D des PEI.
 * Permet de gérer les opérations transverses, calcul de la numérotation, de la dispo, et déclenchement des events, communes aux différents types d'opérations
 * Dans le cadre d'une insertion, on ne peut en aucun cas renseigner ses visites. Le PEI sera donc mis en indisponible.
 * Si un jour, on ajoute la saisie de visites dans la création d'un PEI, il faudra mettre à jour sa disponibilité.
 */
abstract class AbstractCUDPeiUseCase(typeOperation: TypeOperation) : AbstractCUDGeometrieUseCase<PeiData>(typeOperation) {
    @Inject
    lateinit var calculNumerotationUseCase: NumerotationUseCase

    @Inject
    lateinit var getDisponibilitePeiUseCase: GetDisponibilitePeiUseCase

    @Inject
    lateinit var getNumerotationPeiUseCase: GetNumerotationPeiUseCase

    @Inject
    lateinit var computeZoneSpecialeUseCase: ComputeZoneSpecialeUseCase

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var parametresProvider: Provider<ParametresProvider>

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var pibiRepository: PibiRepository

    @Inject lateinit var penaRepository: PenaRepository

    override fun postEvent(element: PeiData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        eventBus.post(PeiModifiedEvent(element.peiId, typeOperation))
    }

    /**
     * Fonction permettant de savoir s'il faut recalculer le numéro interne du PEI car un de ses attributs structurants a été modifié. <br />
     *
     * Cela ne veut pas dire que le numéro interne sera différent, c'est le calcul qui le déterminera.
     */
    private fun needComputeNumeroInterne(element: PeiData): Boolean {
        return element.peiNumeroInterne != element.peiNumeroInterneInitial ||
            calculNumerotationUseCase.needComputeNumeroInterneCommune(element.peiCommuneId, element.peiCommuneIdInitial, element.peiZoneSpecialeId, element.peiZoneSpecialeIdInitial) ||
            calculNumerotationUseCase.needComputeNumeroInterneNatureDeci(element.peiNatureDeciId, element.peiNatureDeciIdInitial) ||
            calculNumerotationUseCase.needComputeNumeroInterneDomaine(element.peiDomaineId, element.peiDomaineIdInitial) ||
            calculNumerotationUseCase.needComputeNumeroInterneGestionnaire(element.peiGestionnaireId, element.peiGestionnaireIdInitial)
    }

    override fun getListGeometrie(element: PeiData): Collection<Geometry> {
        return listOf(element.peiGeometrie)
    }

    override fun ensureSrid(element: PeiData): PeiData {
        if (element.peiGeometrie.srid != appSettings.srid) {
            if (element is PenaData) {
                return element.copy(peiGeometrie = transform(element.peiGeometrie))
            }
            if (element is PibiData) {
                return element.copy(peiGeometrie = transform(element.peiGeometrie))
            }
        }
        return element
    }

    override fun execute(userInfo: WrappedUserInfo, element: PeiData): PeiData {
        if (typeOperation != TypeOperation.DELETE) {
            // Calcul de la zone spéciale
            val computedPeiZoneSpecialeId = computeZoneSpecialeUseCase.computeZoneSpeciale(element.peiGeometrie)
            element.peiZoneSpecialeId = computedPeiZoneSpecialeId
            // Si on est en création OU si on autorise la renumérotation, et qu'elle est nécessaire
            if (element.peiNumeroInterne == null || element.peiNumeroComplet == null ||
                parametresProvider.get().getParametreBoolean(GlobalConstants.PARAM_PEI_RENUMEROTATION_INTERNE_AUTO) == true &&
                needComputeNumeroInterne(element)
            ) {
                val pair = getNumerotationPeiUseCase.execute(element)

                element.peiNumeroInterne = pair.second
                element.peiNumeroComplet = pair.first
            }

            // Si c'est une insertion, on met directement le PEI indisponible
            // (Il n'est pas encore présent en base et n'a pas de visites)
            if (typeOperation == TypeOperation.INSERT) {
                element.peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE
            } else {
                element.peiDisponibiliteTerrestre = getDisponibilitePeiUseCase.execute(element)
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

            penaRepository.deleteLienPenaTypeEngin(peiData.peiId)
            peiData.typeEnginIds?.let { typeEnginIds -> penaRepository.addLienPenaTypeEngin(peiData.peiId, typeEnginIds) }
        }
    }

    /**
     * Méthode permettant de décrire tout ce qui est spécifique à chaque opération, typiquement le service métier à appeler
     */
    protected abstract fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData): Any?

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiData) {
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
