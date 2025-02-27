package remocra.usecase.gestionnaire

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.ContactData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.ContactRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteContactUseCase : AbstractCUDUseCase<ContactData>(TypeOperation.DELETE) {

    @Inject lateinit var contactRepository: ContactRepository

    override fun checkDroits(userInfo: UserInfo) {
        // Les droits sont gérés dans le checkContraintes puisqu'on a besoin de savoir si c'est un gestionnaire ou organisme
    }

    override fun postEvent(element: ContactData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.contactId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CONTACT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: ContactData): ContactData {
        contactRepository.deleteLContactRole(element.contactId)
        if (element.isGestionnaire) {
            contactRepository.deleteLContactGestionnaire(element.contactId)
        } else {
            contactRepository.deleteLContactOrganisme(element.contactId)
        }
        contactRepository.deleteContact(element.contactId)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ContactData) {
        if ((element.isGestionnaire && !userInfo!!.droits.contains(Droit.GEST_SITE_A)) ||
            (!element.isGestionnaire && !userInfo!!.droits.contains(Droit.ADMIN_UTILISATEURS_A))
        ) {
            throw RemocraResponseException(ErrorType.CONTACT_FORBIDDEN_DELETE)
        }
    }
}
