package remocra.usecase.gestionnaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
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

class UpdateGestionnaireUseCase
@Inject
constructor(
    private val gestionnaireRepository: GestionnaireRepository,
) :
    AbstractCUDUseCase<Gestionnaire>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if ((!userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A) && userInfo.typeSourceModification == TypeSourceModification.REMOCRA_WEB) ||
            (!userInfo.hasDroit(droitWeb = Droit.MOBILE_GESTIONNAIRE_C) && userInfo.typeSourceModification == TypeSourceModification.MOBILE)
        ) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: Gestionnaire, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.gestionnaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GESTIONNAIRE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: Gestionnaire): Gestionnaire {
        val gestionnairePreviousActif = gestionnaireRepository.getGestionnairePreviousActif(element.gestionnaireId)
        gestionnaireRepository.upsertGestionnaire(element)
        val contactsIdList = gestionnaireRepository.getContactsIdForGestionnaire(element.gestionnaireId)
        if (gestionnairePreviousActif != element.gestionnaireActif && contactsIdList.isNotEmpty()) {
            gestionnaireRepository.deactivateContacts(contactsIdList, element.gestionnaireActif)
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Gestionnaire) {
        if (gestionnaireRepository.checkCodeExists(element.gestionnaireCode, element.gestionnaireId)) {
            throw RemocraResponseException(ErrorType.ADMIN_GESTIONNAIRE_CODE_EXISTS)
        }
    }
}
