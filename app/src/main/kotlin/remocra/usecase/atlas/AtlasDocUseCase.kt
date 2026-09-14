package remocra.usecase.atlas

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.data.AtlasDirectories
import remocra.data.ImportZIPData
import remocra.db.AtlasRepository
import remocra.db.DocumentRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.db.jooq.remocra.tables.pojos.AtlasDocument
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID
import kotlin.io.path.inputStream
import kotlin.io.path.pathString

class AtlasDocUseCase @Inject constructor(
    private val atlasRepository: AtlasRepository,
    private val documentUtils: DocumentUtils,
    private val documentRepository: DocumentRepository,
    private val appSettings: AppSettings,
    private val transactionManager: TransactionManager,
) : AbstractUseCase() {

    fun importZipEnregistrement(importZIPData: ImportZIPData, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            if (!importZIPData.emptyErrors()) throw AssertionError()
            val repoId = UUID.randomUUID().toString()

            // //////////////////// docs ///////////////////////
            val dossier = GlobalConstants.DOSSIER_DOCUMENT_ATLAS
                .resolve(repoId)
                .resolve(AtlasDirectories.PAGES.name)

            importZIPData.getPages().forEach { item ->
                val docId = UUID.randomUUID()
                val atlasId = UUID.randomUUID()
                item.geometrie.srid = appSettings.srid

                item.contenuPdf.inputStream().use { inputStream ->
                    documentUtils.saveFile(
                        inputStream,
                        item.nomFichierPdf,
                        dossier,
                    )
                }

                documentRepository.insertDocument(
                    Document(
                        documentId = docId,
                        documentDate = dateUtils.now(),
                        documentNomFichier = item.nomFichierPdf,
                        documentRepertoire = dossier.pathString,
                    ),
                )

                atlasRepository.insertAtlasDocuments(
                    AtlasDocument(
                        atlasDocumentId = atlasId,
                        atlasDocumentDocumentId = docId,
                        atlasDocumentActif = item.actif,
                        atlasDocumentGeometrie = item.geometrie,
                    ),
                )
            }
            // //////////////////// annexes ///////////////////////
            val dossierAnnexes = GlobalConstants.DOSSIER_DOCUMENT_ATLAS
                .resolve(repoId)
                .resolve(AtlasDirectories.ANNEXES.name)

            importZIPData.getAnnexes().forEachIndexed { index, item ->
                val docId = UUID.randomUUID()
                val atlasId = UUID.randomUUID()

                item.contenuPdf.inputStream().use { inputStream ->
                    documentUtils.saveFile(
                        inputStream,
                        item.nomFichierPdf,
                        dossierAnnexes,
                    )
                }

                documentRepository.insertDocument(
                    Document(
                        documentId = docId,
                        documentDate = dateUtils.now(),
                        documentNomFichier = item.nomFichierPdf,
                        documentRepertoire = dossierAnnexes.pathString,
                    ),
                )

                atlasRepository.insertAtlasAnnexes(
                    AtlasAnnexe(
                        atlasAnnexeId = atlasId,
                        atlasAnnexeDocumentId = docId,
                        atlasAnnexeActif = item.actif,
                        atlasAnnexeName = item.nomAnnexeFileName,
                        atlasAnnexeOrder = index,
                        atlasAnnexeIsVisible = true, // les docs sont visibles par défaut
                    ),
                )
            }
        }
    }
}
