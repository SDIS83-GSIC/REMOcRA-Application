package remocra.usecase.oldeb

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
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
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

class UpdateOldebUseCase @Inject constructor(
    private val oldebRepository: OldebRepository,
    private val documentRepository: DocumentRepository,
    private val documentUtils: DocumentUtils,
) : AbstractCUDGeometrieUseCase<OldebFormInput>(TypeOperation.UPDATE) {

    override fun getListGeometrie(element: OldebFormInput): Collection<Geometry> {
        return listOf(element.oldeb.oldebGeometrie)
    }

    override fun ensureSrid(element: OldebFormInput): OldebFormInput {
        if (element.oldeb.oldebGeometrie.srid != appSettings.srid) {
            return element.copy(
                oldeb = element.oldeb.copy(
                    oldebGeometrie = transform(element.oldeb.oldebGeometrie),
                ),
            )
        }
        return element
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.OLDEB_U)) {
            throw RemocraResponseException(ErrorType.OLDEB_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: OldebFormInput, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.oldeb.oldebId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.OLDEB,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: OldebFormInput): OldebFormInput {
        // Suppression des suites absentes
        oldebRepository.deleteMissingSuite(
            element.oldeb.oldebId,
            element.visiteList?.flatMap { it.suiteList ?: listOf() }?.map { it.oldebVisiteSuiteId },
        )

        // Suppression des anomalies absentes
        // Table de liaison : tout cramer pour repartir sur des bases saines
        oldebRepository.deleteAnomalie(element.oldeb.oldebId)

        // Suppression des document des visites absentes
        oldebRepository.selectMissingVisite(element.oldeb.oldebId, element.visiteList?.map { it.oldebVisiteId })
            .forEach { visite ->
                val list = oldebRepository.selectMissingVisiteDocument(visite.oldebVisiteId)
                oldebRepository.deleteVisiteDocument(visite.oldebVisiteId)
                documentRepository.deleteDocumentByIds(list.map { it.oldebVisiteDocumentDocumentId })
                documentUtils.deleteDirectory(
                    GlobalConstants.DOSSIER_DOCUMENT_OLD.resolve(element.oldeb.oldebId.toString())
                        .resolve(visite.oldebVisiteId.toString()),
                )
            }

        // Suppression des visites absentes
        oldebRepository.deleteMissingVisite(element.oldeb.oldebId, element.visiteList?.map { it.oldebVisiteId })

        // Suppression des caractéristiques absentes
        // Table de liaison : tout cramer pour repartir sur des bases saines
        oldebRepository.deleteCaracteristique(element.oldeb.oldebId)

        // Suppression de la propriété
        // Table de liaison : tout cramer pour repartir sur des bases saines
        oldebRepository.deletePropriete(element.oldeb.oldebId)

        oldebRepository.updateOldeb(
            Oldeb(
                oldebId = element.oldeb.oldebId,
                oldebGeometrie = element.oldeb.oldebGeometrie,
                oldebCommuneId = element.oldeb.oldebCommuneId,
                oldebCadastreSectionId = element.oldeb.oldebCadastreSectionId,
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

        // Update locataire
        if (element.locataire == null) {
            // Pas de locataire : suppression éventuelle
            oldebRepository.deleteLocataire(element.oldeb.oldebId)
        } else {
            if (oldebRepository.checkLocataireExists(element.oldeb.oldebId)) {
                oldebRepository.updateLocataire(
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
            } else {
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
        }

        // Regroupement documents / visite
        val documentVisiteMap = element.documentList?.groupBy {
            it.name.substringAfter("document_").substringBefore("[]")
        }

        // Create visite
        element.visiteList?.forEach { visite ->
            if (oldebRepository.checkVisiteExists(visite.oldebVisiteId)) {
                oldebRepository.updateVisite(
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
            } else {
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
            }

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
                if (oldebRepository.checkVisiteSuiteExists(suite.oldebVisiteSuiteId)) {
                    oldebRepository.updateVisiteSuite(
                        OldebVisiteSuite(
                            oldebVisiteSuiteId = suite.oldebVisiteSuiteId,
                            oldebVisiteSuiteOldebVisiteId = visite.oldebVisiteId,
                            oldebVisiteSuiteOldebTypeSuiteId = suite.oldebVisiteSuiteOldebTypeSuiteId,
                            oldebVisiteSuiteDate = suite.oldebVisiteSuiteDate,
                            oldebVisiteSuiteObservation = suite.oldebVisiteSuiteObservation,
                        ),
                    )
                } else {
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
            }

            // Suppression des documents absents
            oldebRepository.selectMissingVisiteDocument(
                visite.oldebVisiteId,
                visite.documentList?.map { it.documentId },
            )
                .map { it.oldebVisiteDocumentDocumentId }.takeIf { it.isNotEmpty() }?.let { list ->
                    documentRepository.getDocumentByIds(list).forEach { document ->
                        documentUtils.deleteDirectory(Path(document.documentRepertoire)) // On crame le répertoire du fichier
                    }
                    oldebRepository.deleteMissingVisiteDocument(list)
                    documentRepository.deleteDocumentByIds(list)
                }

            // Nouveaux documents
            documentVisiteMap?.get(visite.oldebVisiteCode)?.forEach { file ->
                val documentId = UUID.randomUUID()
                val repertoire =
                    GlobalConstants.DOSSIER_DOCUMENT_OLD.resolve(element.oldeb.oldebId.toString()).resolve(visite.oldebVisiteId.toString())
                        .resolve(documentId.toString())
                file.inputStream.use {
                    documentUtils.saveFile(it, file.submittedFileName, repertoire)
                }

                documentRepository.insertDocument(
                    Document(
                        documentId = documentId,
                        documentDate = dateUtils.now(),
                        documentNomFichier = file.submittedFileName,
                        documentRepertoire = repertoire.absolutePathString(),
                    ),
                )

                oldebRepository.insertDocumentVisite(UUID.randomUUID(), visite.oldebVisiteId, documentId)
            }
        }
        return element.copy(documentList = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: OldebFormInput) {
        // On fait la vérif de contraintes d'uniticé uniquement si la parcelle est renseignée
        if (element.oldeb.oldebCadastreParcelleId != null && oldebRepository.checkSectionAndParcelleIsUsed(
                element.oldeb.oldebId,
                element.oldeb.oldebCommuneId,
                element.oldeb.oldebCadastreSectionId,
                element.oldeb.oldebCadastreParcelleId,
            )
        ) {
            throw RemocraResponseException(ErrorType.OLDEB_PARCELLE_ALREADY_USED)
        }
    }
}
