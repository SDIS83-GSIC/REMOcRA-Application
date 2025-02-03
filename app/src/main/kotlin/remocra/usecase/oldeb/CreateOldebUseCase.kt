package remocra.usecase.oldeb

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.data.oldeb.OldebFormInput
import remocra.db.DocumentRepository
import remocra.db.OldebRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Oldeb
import remocra.db.jooq.remocra.tables.pojos.OldebCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebLocataire
import remocra.db.jooq.remocra.tables.pojos.OldebPropriete
import remocra.db.jooq.remocra.tables.pojos.OldebVisite
import remocra.db.jooq.remocra.tables.pojos.OldebVisiteAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebVisiteSuite
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class CreateOldebUseCase @Inject constructor(
    private val oldebRepository: OldebRepository,
    private val documentRepository: DocumentRepository,
    private val documentUtils: DocumentUtils,
    private val appSettings: AppSettings,
) : AbstractCUDUseCase<OldebFormInput>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.OLDEB_C)) {
            throw RemocraResponseException(ErrorType.OLDEB_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: OldebFormInput, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(documentList = null),
                pojoId = element.oldeb.oldebId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: OldebFormInput): OldebFormInput {
        // Create OLD
        oldebRepository.insertOldeb(
            Oldeb(
                oldebId = element.oldeb.oldebId,
                oldebGeometrie = element.oldeb.oldebGeometrie,
                oldebCommuneId = element.oldeb.oldebCommuneId,
                oldebCadastraSectionId = element.oldeb.oldebCadastreSectionId,
                oldebCadastreParcelleId = element.oldeb.oldebCadastreParcelleId,
                oldebOldebTypeAccesId = element.oldeb.oldebOldebTypeAccesId,
                oldebOldebTypeZoneUrbanismeId = element.oldeb.oldebOldebTypeZoneUrbanismeId,
                oldebNumVoie = element.oldeb.oldebNumVoie,
                oldebVoieId = element.oldeb.oldebVoieId,
                oldebLieuDitId = element.oldeb.oldebLieuDitId,
                oldebVolume = element.oldeb.oldebVolume,
                oldebLargeurAcces = element.oldeb.oldebLargeurAcces,
                oldebPortailElectrique = element.oldeb.oldebPortailElectrique,
                oldebCodePortail = element.oldeb.oldebCodePortail,
                oldebActif = true,
            ),
        )

        // Caractéristiques OLD
        element.oldeb.caracteristiqueList.forEach { caracteristiqueId ->
            oldebRepository.insertCaracteristique(
                OldebCaracteristique(
                    oldebId = element.oldeb.oldebId,
                    oldebTypeCaracteristiqueId = caracteristiqueId,
                ),
            )
        }

        // Lien propriété / propriétaire / OLD
        oldebRepository.insertPropriete(
            OldebPropriete(
                oldebProprieteId = UUID.randomUUID(),
                oldebProprieteOldebId = element.oldeb.oldebId,
                oldebProprieteOldebProprietaireId = element.propriete!!.oldebProprieteOldebProprietaireId,
                oldebProprieteOldebTypeResidenceId = element.propriete!!.oldebProprieteOldebTypeResidenceId,
            ),
        )

        // Create locataire
        if (element.locataire != null) {
            oldebRepository.insertLocataire(
                OldebLocataire(
                    oldebLocataireId = element.locataire.oldebLocataireId,
                    oldebLocataireOrganisme = element.locataire.oldebLocataireOrganisme,
                    oldebLocataireRaisonSociale = element.locataire.oldebLocataireRaisonSociale.takeIf { element.locataire.oldebLocataireOrganisme },
                    oldebLocataireCivilite = element.locataire.oldebLocataireCivilite,
                    oldebLocataireNom = element.locataire.oldebLocataireNom,
                    oldebLocatairePrenom = element.locataire.oldebLocatairePrenom,
                    oldebLocataireTelephone = element.locataire.oldebLocataireTelephone,
                    oldebLocataireEmail = element.locataire.oldebLocataireEmail,
                    oldebLocataireOldebId = element.oldeb.oldebId,
                ),
            )
        }

        // Regroupement documents / visite
        val documentVisiteMap = element.documentList?.groupBy {
            it.name.substringAfter("document_").substringBefore("[]")
        }

        // Create visite
        element.visiteList?.forEach { visite ->
            oldebRepository.insertVisite(
                OldebVisite(
                    oldebVisiteId = visite.oldebVisiteId,
                    oldebVisiteCode = visite.oldebVisiteCode,
                    oldebVisiteDateVisite = visite.oldebVisiteDateVisite,
                    oldebVisiteAgent = visite.oldebVisiteAgent,
                    oldebVisiteObservation = visite.oldebVisiteObservation,
                    oldebVisiteOldebId = element.oldeb.oldebId,
                    oldebVisiteDebroussaillementParcelleId = visite.oldebVisiteDebroussaillementParcelleId,
                    oldebVisiteDebroussaillementAccesId = visite.oldebVisiteDebroussaillementAccesId,
                    oldebVisiteOldebTypeAvisId = visite.oldebVisiteOldebTypeAvisId,
                    oldebVisiteOldebTypeActionId = visite.oldebVisiteOldebTypeActionId,
                ),
            )

            // Anomalies
            visite.anomalieList?.forEach { anomalieId ->
                oldebRepository.insertVisiteAnomalie(
                    OldebVisiteAnomalie(
                        oldebVisiteId = visite.oldebVisiteId,
                        oldebTypeAnomalieId = anomalieId,
                    ),
                )
            }

            // Suites
            visite.suiteList?.forEach { suite ->
                oldebRepository.insertVisiteSuite(
                    OldebVisiteSuite(
                        oldebVisiteSuiteId = suite.oldebVisiteSuiteId,
                        oldebVisiteSuiteOldebVisiteId = visite.oldebVisiteId,
                        oldebVisiteSuiteOldebTypeSuiteId = suite.oldebVisiteSuiteOldebTypeSuiteId,
                        oldebVisiteSuiteDate = suite.oldebVisiteSuiteDate,
                        oldebVisiteSuiteObservation = suite.oldebVisiteSuiteObservation,
                    ),
                )
            }

            // Nouveaux documents
            documentVisiteMap?.get(visite.oldebVisiteCode)?.forEach { file ->
                val documentId = UUID.randomUUID()
                val repertoire = "${GlobalConstants.DOSSIER_DOCUMENT_OLD}${element.oldeb.oldebId}/${visite.oldebVisiteId}/$documentId"
                documentUtils.saveFile(file.inputStream.readAllBytes(), file.submittedFileName, repertoire)

                documentRepository.insertDocument(
                    Document(
                        documentId = documentId,
                        documentDate = dateUtils.now(),
                        documentNomFichier = file.submittedFileName,
                        documentRepertoire = repertoire,
                    ),
                )

                oldebRepository.insertDocumentVisite(UUID.randomUUID(), visite.oldebVisiteId, documentId)
            }
        }

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: OldebFormInput) {}
}
