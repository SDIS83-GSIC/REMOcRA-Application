package remocra.apimobile.usecase

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.repository.IncomingRepository
import remocra.db.ContactRepository
import remocra.db.GestionnaireRepository
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import java.util.UUID

class ValideIncomingTournee {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    private lateinit var contactRepository: ContactRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tourneeId: UUID) {
        val gestionnaires = incomingRepository.getGestionnaires()

        logger.info("Gestion des gestionnaires")
        gestionGestionnaire(gestionnaires)

        logger.info("Gestion des contacts")
        gestionContact(gestionnaires)
    }

    private fun gestionGestionnaire(gestionnaires: Collection<remocra.db.jooq.incoming.tables.pojos.Gestionnaire>) {
        gestionnaires.forEach {
            logger.info("UPSERT du gestionnaire ${it.gestionnaireId} (${it.gestionnaireCode} - ${it.gestionnaireLibelle}")

            // on doit faire l'insert ou on update
            gestionnaireRepository.upsertGestionnaire(
                Gestionnaire(
                    gestionnaireId = it.gestionnaireId,
                    gestionnaireActif = true,
                    gestionnaireCode = it.gestionnaireCode!!,
                    gestionnaireLibelle = it.gestionnaireLibelle!!,
                ),
            )
        }
    }

    private fun gestionContact(gestionnaires: Collection<remocra.db.jooq.incoming.tables.pojos.Gestionnaire>) {
        // TODO voir pour gérer les communeId, voieId et lieuDitId

        val contacts = incomingRepository.getContacts()
        val contactRole = incomingRepository.getContactRole()

        val contactsInRemocra = contactRepository.getContactWithGestionnaires(gestionnaires.map { it.gestionnaireId })

        contacts.forEach {
            val contact = Contact(
                contactId = it.contactId,
                contactActif = true,
                contactCivilite = it.contactCivilite,
                contactNom = it.contactNom,
                contactPrenom = it.contactPrenom,
                contactNumeroVoie = it.contactNumeroVoie,
                contactSuffixeVoie = it.contactSuffixeVoie,
                contactLieuDitText = it.contactLieuDitText,
                contactLieuDitId = null,
                contactVoieText = it.contactVoieText,
                contactVoieId = null,
                contactCodePostal = it.contactCodePostal,
                contactCommuneText = it.contactCommuneText,
                contactCommuneId = null,
                contactPays = it.contactPays,
                contactTelephone = it.contactTelephone,
                contactEmail = it.contactEmail,
                contactFonctionContactId = it.contactFonctionContactId,
                contactIsCompteService = null, // TODO Champ à prendre en compte dans l'appli mobile ?
            )

            // Si c'est un update
            if (contactsInRemocra.contains(it.contactId)) {
                logger.info("Mise à jour du contact ${it.contactId}")

                contactRepository.updateContact(contact)

                // On supprime les rôles et on les remets
                contactRepository.deleteLContactRole(it.contactId)
            } else {
                logger.info("CREATION du contact ${it.contactId}")
                contactRepository.insertContact(contact)
            }

            // Dans tous les cas, on met ou remets les contacts rôles
            contactRole.filter { lContactRole -> lContactRole.contactId == it.contactId }.forEach {
                contactRepository.insertLContactRole(
                    LContactRole(
                        contactId = it.contactId,
                        roleId = it.roleId,
                    ),
                )
            }
        }
    }
}
