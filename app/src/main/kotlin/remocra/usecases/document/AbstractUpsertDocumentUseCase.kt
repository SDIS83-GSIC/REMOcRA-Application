package remocra.usecases.document

import com.google.inject.Inject
import jakarta.servlet.http.Part
import remocra.auth.UserInfo
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime
import java.util.UUID

abstract class AbstractUpsertDocumentUseCase<T : AbstractDocuments> : AbstractCUDUseCase<T>(TypeOperation.UPDATE) {

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var documentUtils: DocumentUtils

    /**
     * Permet d'insérer le document dans la table de liaison
     */
    abstract fun insertLDocument(documentId: UUID, element: T, newDoc: AbstractDocumentData)

    /**
     * Permet de supprimer une liste de documents dans la table de liaison
     */
    abstract fun deleteLDocument(listeDocsToRemove: Collection<UUID>)

    /**
     * Permet de mettre à jour une liste de documents dans la table de liaison
     */
    abstract fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>)

    /**
     * Retourne le répertoire où le document doit être enregistré
     */
    abstract fun getRepertoire(): String

    override fun execute(userInfo: UserInfo?, element: T): T {
        // On supprime les documents de la liste documentIdToRemove
        val listeDocsToRemove = documentRepository.getDocumentByIds(element.listeDocsToRemove)

        // Sur le serveur
        listeDocsToRemove.forEach {
            documentUtils.deleteFile(it.documentNomFichier, it.documentRepertoire)
        }

        // Puis en base
        deleteLDocument(element.listeDocsToRemove)
        documentRepository.deleteDocumentByIds(element.listeDocsToRemove)

        // On ajoute ceux qui sont à ajouter (l'id est null)
        val nouveauxDocuments = element.listDocument.filter { it.documentId == null }

        nouveauxDocuments.forEach { newDoc ->
            val filePart = element.listDocumentParts.find { it.name == "document_${newDoc.documentNomFichier}" }
            val repertoire = getRepertoire() + "/${element.objectId}"
            documentUtils.saveFile(filePart!!.inputStream.readAllBytes(), newDoc.documentNomFichier, repertoire)

            val idDocument = UUID.randomUUID()
            // On sauvegarde en base
            documentRepository.insertDocument(
                Document(
                    documentId = idDocument,
                    documentDate = ZonedDateTime.now(clock),
                    documentRepertoire = repertoire,
                    documentNomFichier = newDoc.documentNomFichier,
                ),
            )

            insertLDocument(idDocument, element, newDoc)
        }

        val listToUpdate = element.listDocument.minus(nouveauxDocuments)

        updateLDocument(listToUpdate)
        return element
    }
}

abstract class AbstractDocumentData {
    abstract val documentId: UUID?
    abstract val documentNomFichier: String
}

abstract class AbstractDocuments {
    abstract val objectId: UUID
    abstract val listeDocsToRemove: List<UUID>
    abstract val listDocument: List<AbstractDocumentData>
    abstract val listDocumentParts: List<Part>
}
