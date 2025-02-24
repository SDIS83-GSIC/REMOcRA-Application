package remocra.usecase.crise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.CriseData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CriseData) {
    }

    override fun execute(userInfo: UserInfo?, element: CriseData): CriseData {
        criseRepository.createCrise(element)

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
        return element
    }

    override fun postEvent(element: CriseData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.criseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CRISE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }
}
