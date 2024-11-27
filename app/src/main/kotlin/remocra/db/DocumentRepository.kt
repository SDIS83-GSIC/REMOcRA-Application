package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_PEI_DOCUMENT
import java.util.UUID

class DocumentRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getDocumentByPei(peiId: UUID): Collection<DocumentPei> =
        dsl.select(
            DOCUMENT.ID,
            DOCUMENT.NOM_FICHIER,
            DOCUMENT.REPERTOIRE,
            L_PEI_DOCUMENT.IS_PHOTO_PEI,
        )
            .from(DOCUMENT)
            .join(L_PEI_DOCUMENT)
            .on(L_PEI_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(L_PEI_DOCUMENT.PEI_ID.eq(peiId))
            .fetchInto()

    fun getById(documentId: UUID): Document? =
        dsl.selectFrom(DOCUMENT)
            .where(DOCUMENT.ID.eq(documentId))
            .fetchOneInto()

    fun getDocumentByIds(listId: List<UUID>): Collection<Document> =
        dsl.selectFrom(DOCUMENT)
            .where(DOCUMENT.ID.`in`(listId))
            .fetchInto()

    fun deleteDocumentByIds(listId: List<UUID>) =
        dsl.deleteFrom(DOCUMENT)
            .where(DOCUMENT.ID.`in`(listId))
            .execute()

    fun deleteDocumentPei(listId: Collection<UUID>) =
        dsl.deleteFrom(L_PEI_DOCUMENT)
            .where(L_PEI_DOCUMENT.DOCUMENT_ID.`in`(listId))
            .execute()

    fun insertDocument(document: Document) =
        dsl.insertInto(DOCUMENT)
            .set(dsl.newRecord(DOCUMENT, document))
            .execute()

    fun updateDocument(documentNomFichier: String, repertoire: String, documentId: UUID) =
        dsl.update(DOCUMENT)
            .set(DOCUMENT.NOM_FICHIER, documentNomFichier)
            .set(DOCUMENT.REPERTOIRE, repertoire)
            .where(DOCUMENT.ID.eq(documentId))
            .execute()

    fun insertDocumentPei(peiId: UUID, documentId: UUID, isPhotoPei: Boolean) =
        dsl.insertInto(L_PEI_DOCUMENT)
            .set(L_PEI_DOCUMENT.PEI_ID, peiId)
            .set(L_PEI_DOCUMENT.DOCUMENT_ID, documentId)
            .set(L_PEI_DOCUMENT.IS_PHOTO_PEI, isPhotoPei)
            .execute()

    fun updateIsPhotoPei(listDocumentId: List<UUID>, isPhotoPei: Boolean) =
        dsl.update(L_PEI_DOCUMENT)
            .set(L_PEI_DOCUMENT.IS_PHOTO_PEI, isPhotoPei)
            .where(L_PEI_DOCUMENT.DOCUMENT_ID.`in`(listDocumentId))
            .execute()

    data class DocumentPei(
        val documentId: UUID,
        val documentNomFichier: String,
        val documentRepertoire: String,
        val isPhotoPei: Boolean = false,
    )

    fun upsertDocument() {
    }

    fun getCourrierIdByDocumentId(documentId: UUID): UUID? =
        dsl.select(COURRIER.ID)
            .from(COURRIER)
            .where(COURRIER.DOCUMENT_ID.eq(documentId))
            .fetchOneInto()
}
