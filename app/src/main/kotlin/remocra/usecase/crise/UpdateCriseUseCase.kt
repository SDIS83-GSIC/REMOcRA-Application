package remocra.usecase.crise

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.CriseData
import remocra.data.enums.ErrorType
import remocra.db.CriseRepository
import remocra.db.ToponymieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateCriseUseCase : AbstractCUDUseCase<CriseData>(TypeOperation.UPDATE) {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var toponymieRepository: ToponymieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CRISE_U)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: CriseData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.criseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CRISE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: CriseData): CriseData {
        // L_CRISE_COMMUNE
        criseRepository.deleleteLCriseCommune(element.criseId)
        // L_TOPONYMIE_CRISE
        criseRepository.deleteLToponymieCrise(element.criseId)
        // L_COUCHES_CRISE
        criseRepository.deleteLCoucheCrise(element.criseId)

        // On remplit les L_CRISE_COMMUNE
        criseRepository.insertLCriseCommune(
            element.criseId,
            element.listeCommuneId,
        )
        if (element.listeToponymieId != null) {
            // On remplit les L_TOPONYMIE_CRISE
            toponymieRepository.insertLToponymieCrise(
                element.listeToponymieId,
                element.criseId,
            )
        }

        if (!element.couchesWMS.isNullOrEmpty()) {
            // On remplit les L_COUCHES_CRISE
            criseRepository.insertLCoucheCrise(
                element.criseId,
                element.couchesWMS,
            )
        }

        // On update la crise
        criseRepository.updateCrise(
            criseId = element.criseId,
            criseLibelle = element.criseLibelle,
            criseDescription = element.criseDescription,
            criseDateDebut = element.criseDateDebut,
            criseDateFin = element.criseDateFin,
            criseTypeCriseId = element.criseTypeCriseId,
            criseStatutType = element.criseStatutType,
        )
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CriseData) {
        // no-op
    }
}
