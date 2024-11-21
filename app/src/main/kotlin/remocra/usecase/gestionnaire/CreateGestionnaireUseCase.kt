package remocra.usecase.gestionnaire

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.GestionnaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateGestionnaireUseCase : AbstractCUDUseCase<Gestionnaire>(TypeOperation.INSERT) {

    @Inject lateinit var gestionnaireRepository: GestionnaireRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: Gestionnaire, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.gestionnaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GESTIONNAIRE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: Gestionnaire): Gestionnaire {
        gestionnaireRepository.insertGestionnaire(element)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Gestionnaire) {
        if (gestionnaireRepository.checkCodeExists(element.gestionnaireCode, null)) {
            throw RemocraResponseException(ErrorType.ADMIN_GESTIONNAIRE_CODE_EXISTS)
        }
    }
}
