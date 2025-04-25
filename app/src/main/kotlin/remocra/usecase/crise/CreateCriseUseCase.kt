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

class CreateCriseUseCase : AbstractCUDUseCase<CriseData>(TypeOperation.INSERT) {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var toponymieRepository: ToponymieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: CriseData) {
    }

    override fun execute(userInfo: WrappedUserInfo, element: CriseData): CriseData {
        criseRepository.createCrise(element)

        // On remplit les L_CRISE_COMMUNE
        criseRepository.insertLCriseCommune(
            element.criseId,
            element.listeCommuneId,
        )

        // On remplit les L_COUCHES_CRISES

        criseRepository.insertLCoucheCrise(
            element.criseId,
            element.couchesWMS,
        )

        if (element.listeToponymieId != null) {
            // On remplit les L_TOPONYMIE_CRISE
            toponymieRepository.insertLToponymieCrise(
                element.listeToponymieId,
                element.criseId,
            )
        }
        return element
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
}
