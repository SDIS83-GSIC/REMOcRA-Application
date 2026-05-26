package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.ContactData
import remocra.db.ContactRepository
import remocra.db.GestionnaireRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.log.LogManager
import remocra.usecase.AbstractUseCase
import remocra.usecase.gestionnaire.CreateContactUseCase
import remocra.usecase.gestionnaire.CreateGestionnaireUseCase
import remocra.usecase.gestionnaire.UpdateContactUseCase
import remocra.usecase.gestionnaire.UpdateGestionnaireUseCase
import java.util.UUID

class ValideIncomingGestionnairesUseCase @Inject constructor(
    private val incomingRepository: IncomingRepository,
    private val gestionnaireRepository: GestionnaireRepository,
    private val contactRepository: ContactRepository,
    private val createGestionnaireUseCase: CreateGestionnaireUseCase,
    private val updateGestionnaireUseCase: UpdateGestionnaireUseCase,
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
) : AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo, logManager: LogManager, gestionnaireId: UUID, mainTransaction: TransactionManager) {
        mainTransaction.transactionResult {
            val gestionnaire = incomingRepository.getGestionnaire(gestionnaireId)

            if (gestionnaire == null) {
                logManager.error("Impossible de trouver le gestionnaire $gestionnaireId dans incoming")
                return@transactionResult
            }

            logManager.info("Gestion du gestionnaire $gestionnaireId")
            val pasErreurGestionnaire = gestionGestionnaire(gestionnaire, logManager, userInfo, mainTransaction)

            logManager.info("Gestion des contacts")
            val pasErreurContact = gestionContact(gestionnaire, logManager, userInfo, mainTransaction)

            if (pasErreurContact && pasErreurGestionnaire) {
                logManager.info("Suppression des gestionnaires")
                incomingRepository.deleteGestionnaire(gestionnaireId)
            }
        }
    }

    /**
     * Retourne un booléen précisant si le gestionnaire a bien été inséré ou mise à jour sans erreur
     * * si true => pas d'erreur
     * * si false => quelque chose s'est mal passé
     */
    private fun gestionGestionnaire(
        gestionnaire: remocra.db.jooq.incoming.tables.pojos.Gestionnaire,
        logManager: LogManager,
        userInfo: WrappedUserInfo,
        mainTransaction: TransactionManager,
    ): Boolean {
        logManager.info("UPSERT du gestionnaire ${gestionnaire.gestionnaireId} (${gestionnaire.gestionnaireCode} - ${gestionnaire.gestionnaireLibelle})")

        val exists = gestionnaireRepository.checkExists(gestionnaire.gestionnaireId)

        val gestionnaireRemocra = Gestionnaire(
            gestionnaireId = gestionnaire.gestionnaireId,
            gestionnaireActif = true,
            gestionnaireCode = gestionnaire.gestionnaireCode,
            gestionnaireLibelle = gestionnaire.gestionnaireLibelle,
        )

        val result = if (!exists) {
            logManager.info("Créaton du gestionnaire ${gestionnaire.gestionnaireId}")
            createGestionnaireUseCase.execute(userInfo, gestionnaireRemocra, mainTransaction)
        } else {
            logManager.info("Mise à jour du gestionnaire ${gestionnaire.gestionnaireId}")
            updateGestionnaireUseCase.execute(userInfo, gestionnaireRemocra, mainTransaction)
        }

        if (result is Result.Error) {
            logManager.error("Erreur lors de la création ou mise à jour du gestionnaire ${gestionnaire.gestionnaireId} : ${result.message}")
            return false
        }

        return true
    }

    /**
     * Retourne un booléen précisant s'il n'y a pas d'erreur
     * * si true => pas d'erreur
     * * si false => quelque chose s'est mal passé
     */
    private fun gestionContact(
        gestionnaire: remocra.db.jooq.incoming.tables.pojos.Gestionnaire,
        logManager: LogManager,
        userInfo: WrappedUserInfo,
        mainTransaction: TransactionManager,
    ): Boolean {
        // TODO voir pour gérer les communeId, voieId et lieuDitId

        val contacts = incomingRepository.getContacts(gestionnaire.gestionnaireId)
        val contactRole = incomingRepository.getContactRole(gestionnaire.gestionnaireId)

        val contactsInRemocra = contactRepository.getContactWithGestionnaire(gestionnaire.gestionnaireId)

        val listContactEnErreur = mutableSetOf<UUID>()
        contacts.forEach {
            val contact = ContactData(
                contactId = it.contactId,
                contactActif = true,
                contactCivilite = it.contactCivilite,
                contactNom = it.contactNom,
                contactPrenom = it.contactPrenom,
                contactNumeroVoie = it.contactNumeroVoie,
                contactSuffixe = it.contactSuffixeVoie,
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
                contactIsCompteService = false, // TODO Champ à prendre en compte dans l'appli mobile ?
                isGestionnaire = true,
                appartenanceId = gestionnaire.gestionnaireId,
                siteId = null,
                listRoleId = contactRole.filter { lContactRole -> lContactRole.contactId == it.contactId }.map { lContactRole -> lContactRole.roleId },
            )

            // Si c'est un update
            val result = if (!contactsInRemocra.contains(it.contactId)) {
                logManager.info("Mise à jour du contact ${it.contactId} : $contact")

                createContactUseCase.execute(userInfo, contact, mainTransaction)
            } else {
                logManager.info("CREATION du contact ${it.contactId} : $contact")
                updateContactUseCase.execute(userInfo, contact, mainTransaction)
            }

            if (result is Result.Error) {
                logManager.error("Erreur leur de l'insertion / mise à jour du contact ${it.contactId} : $contact")
                listContactEnErreur.add(it.contactId)
            }
        }

        // Suppression des contacts
        logManager.info("Suppression des contacts")
        val listeContactId = contacts.map { it.contactId } - listContactEnErreur
        incomingRepository.deleteContactRole(listeContactId)
        incomingRepository.deleteContact(listeContactId)

        return listeContactId.isEmpty()
    }
}
