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
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateContactUseCase : AbstractCUDUseCase<ContactData>(TypeOperation.UPDATE) {

    @Inject lateinit var contactRepository: ContactRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_FORBIDDEN_UPDATE)
        }
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
        contactRepository.updateContact(
            Contact(
                contactId = element.contactId,
                contactActif = element.contactActif,
                contactCivilite = element.contactCivilite,
                contactFonction = element.contactFonction,
                contactNom = element.contactNom,
                contactPrenom = element.contactPrenom,
                contactNumeroVoie = element.contactNumeroVoie,
                contactSuffixeVoie = element.contactSuffixe,
                contactLieuDitText = element.contactLieuDitText.takeIf { element.contactLieuDitId == null },
                contactLieuDitId = element.contactLieuDitId,
                contactVoieText = element.contactVoieText.takeIf { element.contactVoieId == null },
                contactVoieId = element.contactVoieId,
                contactCodePostal = element.contactCodePostal.takeIf { element.contactCommuneId == null },
                contactCommuneText = element.contactCommuneText.takeIf { element.contactCommuneId == null },
                contactCommuneId = element.contactCommuneId,
                contactPays = element.contactPays,
                contactTelephone = element.contactTelephone,
                contactEmail = element.contactEmail,
            ),
        )

        // Update site
        contactRepository.updateSite(element.contactId, element.siteId)

        // On supprime les rôles et on les remets
        contactRepository.deleteLContactRole(element.contactId)

        // Puis des rôles
        element.listRoleId.forEach {
            contactRepository.insertLContactRole(
                LContactRole(
                    contactId = element.contactId,
                    roleId = it,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ContactData) {
        // Pas de contraintes
    }
}
