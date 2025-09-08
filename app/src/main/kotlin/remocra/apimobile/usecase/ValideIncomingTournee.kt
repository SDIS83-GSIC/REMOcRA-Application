package remocra.apimobile.usecase

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.CreationVisiteCtrl
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.VisiteData
import remocra.db.ContactRepository
import remocra.db.DocumentRepository
import remocra.db.DomaineRepository
import remocra.db.GestionnaireRepository
import remocra.db.TourneeRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.usecase.AbstractUseCase
import remocra.usecase.pei.CreatePeiUseCase
import remocra.usecase.visites.CreateVisiteUseCase
import java.util.UUID

class ValideIncomingTournee : AbstractUseCase() {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    private lateinit var contactRepository: ContactRepository

    @Inject
    private lateinit var documentRepository: DocumentRepository

    @Inject
    private lateinit var domaineRepository: DomaineRepository

    @Inject
    private lateinit var tourneeRepository: TourneeRepository

    @Inject
    private lateinit var transactionManager: TransactionManager

    @Inject
    private lateinit var createPeiUseCase: CreatePeiUseCase

    @Inject
    private lateinit var createVisiteUseCase: CreateVisiteUseCase

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo) {
        transactionManager.transactionResult {
            val gestionnaires = incomingRepository.getGestionnaires()

            logger.info("Gestion des gestionnaires")
            gestionGestionnaire(gestionnaires)

            logger.info("Gestion des contacts")
            gestionContact(gestionnaires)

            logger.info("Suppression des gestionnaires")
            incomingRepository.deleteGestionnaire(gestionnaires.map { it.gestionnaireId })

            logger.info("Gestion des nouveaux PEI")
            gestionNewPei(userInfo)

            logger.info("Gestion des photos")
            gestionPhoto(tourneeId)

            logger.info("Gestion des visites")
            gestionVisites(tourneeId, userInfo)

            logger.info("Mise à jour de la tournée $tourneeId")
            tourneeRepository.setAvancementTournee(tourneeId, 100)
            tourneeRepository.desaffectationTournee(tourneeId)

            tourneeRepository.updateDateSynchronisation(dateUtils.now(), tourneeId)

            // On supprime les tournées qui n'ont plus de visites associées
            val listeIdTournee = incomingRepository.getTourneeSansVisite()
            logger.info("Suppression des tournées dont toutes les visites ont été intégrées : ${listeIdTournee.joinToString(", ")}")
            incomingRepository.deleteTournee(listeIdTournee)
        }
    }

    private fun gestionGestionnaire(gestionnaires: Collection<remocra.db.jooq.incoming.tables.pojos.Gestionnaire>) {
        gestionnaires.forEach {
            logger.info("UPSERT du gestionnaire ${it.gestionnaireId} (${it.gestionnaireCode} - ${it.gestionnaireLibelle})")

            val gestionnaire = Gestionnaire(
                gestionnaireId = it.gestionnaireId,
                gestionnaireActif = true,
                gestionnaireCode = it.gestionnaireCode,
                gestionnaireLibelle = it.gestionnaireLibelle,
            )

            // on doit faire l'insert ou on update
            gestionnaireRepository.upsertGestionnaire(
                gestionnaire,
            )

            logger.info("POJO - mise à jour / création d'un gestionnaire : {}", gestionnaire)
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
                logger.info("Mise à jour du contact ${it.contactId} : $contact")

                contactRepository.updateContact(contact)

                // On supprime les rôles et on les remets
                contactRepository.deleteLContactRole(it.contactId)
            } else {
                logger.info("CREATION du contact ${it.contactId} : $contact")
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

        // Suppression des contacts
        logger.info("Suppression des contacts")
        val listeContactId = contacts.map { it.contactId }
        incomingRepository.deleteContactRole(listeContactId)
        incomingRepository.deleteContact(listeContactId)
    }

    private fun gestionNewPei(userInfo: WrappedUserInfo) {
        val listeNewPei = incomingRepository.getNewPei()

        // TODO prendre en compte le domaine proprement
        val domaineId = domaineRepository.getFistDomaineId()

        val peiIdInseres = mutableListOf<UUID>()

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
                        pibiIdentifiantGestionnaire = null,
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

            logger.info("POJO - création d'un PEI : {}", peiData)
            // On délègue la création à notre superbe usecase
            val result = createPeiUseCase.execute(
                userInfo,
                peiData,
                transactionManager,
            )

            if (result !is Result.Success && result !is Result.Created) {
                if (result is Result.Error) {
                    logger.error("Erreur lors de l'insertion du PEI ${it.newPeiId} : ${result.message}")
                }
            } else {
                peiIdInseres.add(it.newPeiId)
            }
        }

        // Suppression des newPei
        logger.info("Suppression des nouveaux PEI")
        incomingRepository.deleteNewPei(listeNewPei.map { it.newPeiId })
    }

    private fun gestionVisites(tourneeId: UUID, userInfo: WrappedUserInfo) {
        val visites = incomingRepository.getVisites(tourneeId)
        val visitesCtrlDebitPression = incomingRepository.getVisitesCtrlDebitPression(tourneeId)
        val visiteAnomalie = incomingRepository.getVisitesAnomalie(tourneeId)

        val visiteIdInseres = mutableListOf<UUID>()

        visites.forEach {
            val ctrl = visitesCtrlDebitPression
                .firstOrNull { v -> v.visiteCtrlDebitPressionVisiteId == it.visiteId }

            val result = createVisiteUseCase.execute(
                userInfo,
                VisiteData(
                    visiteId = it.visiteId,
                    visitePeiId = it.visitePeiId,
                    visiteDate = it.visiteDate,
                    visiteTypeVisite = it.visiteTypeVisite,
                    visiteAgent1 = it.visiteAgent1,
                    visiteAgent2 = it.visiteAgent2,
                    visiteObservation = it.visiteObservation,
                    listeAnomalie = visiteAnomalie.filter { va -> va.visiteId == it.visiteId }.map { it.anomalieId },
                    isCtrlDebitPression = visitesCtrlDebitPression.map { it.visiteCtrlDebitPressionVisiteId }.contains(it.visiteId),
                    ctrlDebitPression = CreationVisiteCtrl(
                        ctrl?.visiteCtrlDebitPressionDebit,
                        ctrl?.visiteCtrlDebitPressionPression,
                        ctrl?.visiteCtrlDebitPressionPressionDyn,
                    ),
                ),
                transactionManager,
            )

            if (result !is Result.Success && result !is Result.Created) {
                if (result is Result.Error) {
                    logger.error("Erreur lors de l'insertion de la visite ${it.visiteId} : ${result.message}")
                }
                logger.error("Erreur lors de l'insertion de la visite ${it.visiteId}")
            } else {
                visiteIdInseres.add(it.visiteId)
            }
        }

        // Suppression des visites
        logger.info("Suppression des visites")
        incomingRepository.deleteVisiteAnomalie(visiteIdInseres)
        incomingRepository.deleteVisiteCtrlDebitPression(visiteIdInseres)
        incomingRepository.deleteVisite(visiteIdInseres)
    }

    private fun gestionPhoto(tourneeId: UUID) {
        val listePhotoPei = incomingRepository.getPhotoPei(tourneeId)

        listePhotoPei.forEach {
            logger.info("CREATION document ${it.photoId} pour le PEI ${it.peiId}")
            documentRepository.insertDocument(
                Document(
                    documentId = it.photoId,
                    documentDate = it.photoDate,
                    documentNomFichier = it.photoLibelle,
                    documentRepertoire = it.photoPath,
                ),
            )

            // On insère ensuite le lien avec isPhotoPei
            documentRepository.insertDocumentPei(
                peiId = it.peiId,
                documentId = it.photoId,
                isPhotoPei = true,
            )
        }

        logger.info("Suppression des photos")
        incomingRepository.deletePhotoPei(listePhotoPei.map { it.photoId })
    }
}
