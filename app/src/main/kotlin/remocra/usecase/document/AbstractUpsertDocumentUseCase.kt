package remocra.usecase.document

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.AbstractDocumentData
import remocra.data.AbstractDocuments
import remocra.db.DocumentRepository
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

abstract class AbstractUpsertDocumentUseCase<T : AbstractDocuments> : AbstractCUDUseCase<T>(TypeOperation.UPDATE) {

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var documentUtils: DocumentUtils

    /**
     * Permet d'insérer le document dans la table de liaison
     */
    abstract fun insertLDocument(documentId: UUID, element: T, newDoc: AbstractDocumentData, mainTransactionManager: TransactionManager?)

    /**
     * Permet de supprimer une liste de documents dans la table de liaison
     */
    abstract fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?)

    /**
     * Permet de mettre à jour une liste de documents dans la table de liaison
     */
    abstract fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?)

    /**
     * Retourne le répertoire où le document doit être enregistré
     */
    abstract fun getRepertoire(): String

    override fun execute(userInfo: WrappedUserInfo, element: T): T {
        // On supprime les documents de la liste documentIdToRemove
        val listeDocsToRemove = documentRepository.getDocumentByIds(element.listeDocsToRemove)

        // Sur le serveur
        listeDocsToRemove.forEach {
            documentUtils.deleteFile(it.documentNomFichier, it.documentRepertoire)
        }

        // Puis en base
        deleteLDocument(element.listeDocsToRemove, transactionManager)
        documentRepository.deleteDocumentByIds(element.listeDocsToRemove)

        // On ajoute ceux qui sont à ajouter (l'id est null)
        val nouveauxDocuments = element.listDocument.filter { it.documentId == null }

        nouveauxDocuments.forEach { newDoc ->
            val filePart = element.listDocumentParts.find { it.name == "document_${newDoc.documentNomFichier}" }
            val repertoire = getRepertoire() + "${element.objectId}"
            documentUtils.saveFile(filePart!!.inputStream.readAllBytes(), newDoc.documentNomFichier, repertoire)

            val idDocument = UUID.randomUUID()
            // On sauvegarde en base
            documentRepository.insertDocument(
                Document(
                    documentId = idDocument,
                    documentDate = dateUtils.now(),
                    documentRepertoire = repertoire,
                    documentNomFichier = newDoc.documentNomFichier,
                ),
            )

            insertLDocument(idDocument, element, newDoc, transactionManager)
        }

        val listToUpdate = element.listDocument.minus(nouveauxDocuments)

        updateLDocument(listToUpdate, transactionManager)
        return element
    }
}
