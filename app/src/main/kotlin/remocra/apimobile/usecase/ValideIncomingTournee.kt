package remocra.apimobile.usecase

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.ContactRepository
import remocra.db.DomaineRepository
import remocra.db.GestionnaireRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.usecase.pei.CreatePeiUseCase
import java.util.UUID

class ValideIncomingTournee {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    private lateinit var contactRepository: ContactRepository

    @Inject
    private lateinit var domaineRepository: DomaineRepository

    @Inject
    private lateinit var transactionManager: TransactionManager

    @Inject
    private lateinit var createPeiUseCase: CreatePeiUseCase

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tourneeId: UUID, userInfo: UserInfo) {
        transactionManager.transactionResult {
            val gestionnaires = incomingRepository.getGestionnaires()

            logger.info("Gestion des gestionnaires")
            gestionGestionnaire(gestionnaires)

            logger.info("Gestion des contacts")
            gestionContact(gestionnaires)

            logger.info("Gestion des nouveaux PEI")
            gestionNewPei(userInfo)
        }
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

    private fun gestionNewPei(userInfo: UserInfo) {
        val listeNewPei = incomingRepository.getNewPei()

        // TODO prendre en compte le domaine proprement
        val domaineId = domaineRepository.getFistDomaineId()

        listeNewPei.forEach {
            logger.info("CREATION d'un PEI ${it.newPeiId}")
            val peiData: PeiData =
                if (it.newPeiTypePei == TypePei.PIBI) {
                    PibiData(
                        peiId = it.newPeiId,
                        peiNumeroComplet = null,
                        peiNumeroInterne = null,
                        peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE,
                        peiTypePei = it.newPeiTypePei,
                        peiGeometrie = it.newPeiGeometrie,
                        peiAutoriteDeciId = null,
                        peiServicePublicDeciId = null,
                        peiMaintenanceDeciId = null,
                        peiCommuneId = it.newPeiCommuneId,
                        peiVoieId = it.newPeiVoieId,
                        peiNumeroVoie = null,
                        peiSuffixeVoie = null,
                        peiVoieTexte = null,
                        peiLieuDitId = it.newPeiLieuDitId,
                        peiCroisementId = null,
                        peiComplementAdresse = null,
                        peiEnFace = false,
                        peiDomaineId = domaineId,
                        peiNatureId = it.newPeiNatureId,
                        peiSiteId = null,
                        peiGestionnaireId = it.newPeiGestionnaireId,
                        peiNatureDeciId = it.newPeiNatureDeciId,
                        peiZoneSpecialeId = null,
                        peiAnneeFabrication = null,
                        peiNiveauId = null,
                        peiObservation = it.newPeiObservation,
                        peiNumeroInterneInitial = null,
                        peiCommuneIdInitial = null,
                        peiZoneSpecialeIdInitial = null,
                        peiNatureDeciIdInitial = null,
                        peiDomaineIdInitial = null,
                        pibiDiametreId = null,
                        pibiServiceEauId = null,
                        pibiNumeroScp = null,
                        pibiRenversable = false,
                        pibiDispositifInviolabilite = false,
                        pibiModeleId = null,
                        pibiMarqueId = null,
                        pibiReservoirId = null,
                        pibiDebitRenforce = false,
                        pibiTypeCanalisationId = null,
                        pibiTypeReseauId = null,
                        pibiDiametreCanalisation = null,
                        pibiSurpresse = false,
                        pibiAdditive = false,
                        pibiJumeleId = null,
                    )
                } else {
                    PenaData(
                        peiId = it.newPeiId,
                        peiNumeroComplet = null,
                        peiNumeroInterne = null,
                        peiDisponibiliteTerrestre = Disponibilite.INDISPONIBLE,
                        peiTypePei = it.newPeiTypePei,
                        peiGeometrie = it.newPeiGeometrie,
                        peiAutoriteDeciId = null,
                        peiServicePublicDeciId = null,
                        peiMaintenanceDeciId = null,
                        peiCommuneId = it.newPeiCommuneId,
                        peiVoieId = it.newPeiVoieId,
                        peiNumeroVoie = null,
                        peiSuffixeVoie = null,
                        peiVoieTexte = null,
                        peiLieuDitId = it.newPeiLieuDitId,
                        peiCroisementId = null,
                        peiComplementAdresse = null,
                        peiEnFace = false,
                        peiDomaineId = domaineId,
                        peiNatureId = it.newPeiNatureId,
                        peiSiteId = null,
                        peiGestionnaireId = it.newPeiGestionnaireId,
                        peiNatureDeciId = it.newPeiNatureDeciId,
                        peiZoneSpecialeId = null,
                        peiAnneeFabrication = null,
                        peiNiveauId = null,
                        peiObservation = it.newPeiObservation,
                        peiNumeroInterneInitial = null,
                        peiCommuneIdInitial = null,
                        peiZoneSpecialeIdInitial = null,
                        peiNatureDeciIdInitial = null,
                        peiDomaineIdInitial = null,
                        penaDisponibiliteHbe = Disponibilite.INDISPONIBLE,
                        penaCapacite = null,
                        penaCapaciteIllimitee = false,
                        penaCapaciteIncertaine = false,
                        penaQuantiteAppoint = null,
                        penaMateriauId = null,
                    )
                }

            // On délègue la création à notre superbe usecase
            createPeiUseCase.execute(
                userInfo,
                peiData,
                transactionManager,
            )
        }
    }
}
