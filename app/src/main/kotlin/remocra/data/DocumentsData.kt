package remocra.data

import jakarta.servlet.http.Part
import java.util.UUID

/**
 * Classes permettant de manipuler les documents
 */

class DocumentsData {
    open class DocumentsEtude(
        override val objectId: UUID,
        override val listeDocsToRemove: List<UUID>,
        override val listDocument: List<DocumentEtudeData>,
        override val listDocumentParts: List<Part>,
    ) : AbstractDocuments()

    open class DocumentEtudeData(
        override val documentId: UUID?,
        override val documentNomFichier: String,
        val etudeDocumentLibelle: String?,
    ) : AbstractDocumentData()

    open class DocumentsPei(
        override val objectId: UUID,
        override val listeDocsToRemove: List<UUID>,
        override val listDocument: List<DocumentData>,
        override val listDocumentParts: List<Part>,
    ) : AbstractDocuments()

    open class DocumentData(
        override val documentId: UUID?,
        override val documentNomFichier: String,
        val isPhotoPei: Boolean,
    ) : AbstractDocumentData()

    open class DocumentsPermis(
        override val objectId: UUID,
        override val listeDocsToRemove: List<UUID>,
        override val listDocument: List<DocumentPermisData>,
        override val listDocumentParts: List<Part>,
    ) : AbstractDocuments()

    open class DocumentPermisData(
        override val documentId: UUID?,
        override val documentNomFichier: String,
    ) : AbstractDocumentData()

    open class DocumentsModeleCourrier(
        override val objectId: UUID,
        override val listeDocsToRemove: List<UUID>,
        override val listDocument: List<DocumentModeleCourrierData>,
        override val listDocumentParts: List<Part>,
    ) : AbstractDocuments()

    open class DocumentModeleCourrierData(
        override val documentId: UUID?,
        override val documentNomFichier: String,
        val isMainReport: Boolean,
        val documentRepertoire: String?,
    ) : AbstractDocumentData()
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
