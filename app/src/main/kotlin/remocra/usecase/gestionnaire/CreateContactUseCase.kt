package remocra.usecase.gestionnaire

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.ContactData
import remocra.data.enums.ErrorType
import remocra.db.ContactRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.LContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactOrganisme
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateContactUseCase : AbstractCUDUseCase<ContactData>(TypeOperation.INSERT) {

    @Inject lateinit var contactRepository: ContactRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Les droits sont gérés dans le checkContraintes puisqu'on a besoin de savoir si c'est un gestionnaire ou organisme
    }

    override fun postEvent(element: ContactData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.contactId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CONTACT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: ContactData): ContactData {
        contactRepository.insertContact(
            Contact(
                contactId = element.contactId,
                contactActif = element.contactActif,
                contactCivilite = element.contactCivilite,
                contactFonctionContactId = element.contactFonctionContactId,
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
                contactIsCompteService = element.contactIsCompteService,
            ),
        )

        if (element.isGestionnaire) {
            // insertion du lien entre le gestionnaire et le contact
            contactRepository.insertLContactGestionnaire(
                LContactGestionnaire(
                    contactId = element.contactId,
                    gestionnaireId = element.appartenanceId,
                    siteId = element.siteId,
                ),
            )
        } else {
            contactRepository.insertLContactOrganisme(
                LContactOrganisme(
                    contactId = element.contactId,
                    organismeId = element.appartenanceId,
                ),
            )
        }

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

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ContactData) {
        if ((element.isGestionnaire && !userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A)) ||
            (!element.isGestionnaire && !userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A))
        ) {
            throw RemocraResponseException(ErrorType.CONTACT_FORBIDDEN_INSERT)
        }
    }
}
