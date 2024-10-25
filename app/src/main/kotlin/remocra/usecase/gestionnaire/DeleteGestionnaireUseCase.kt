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

class DeleteGestionnaireUseCase : AbstractCUDUseCase<Gestionnaire>(TypeOperation.DELETE) {

    @Inject lateinit var gestionnaireRepository: GestionnaireRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_FORBIDDEN_DELETE)
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
        gestionnaireRepository.deleteGestionnaire(element.gestionnaireId)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Gestionnaire) {
        if (gestionnaireRepository.gestionnaireUsedInPei(element.gestionnaireId)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_USED_IN_PEI)
        }

        if (gestionnaireRepository.gestionnaireUsedInSite(element.gestionnaireId)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_USED_IN_SITE)
        }
    }
}
