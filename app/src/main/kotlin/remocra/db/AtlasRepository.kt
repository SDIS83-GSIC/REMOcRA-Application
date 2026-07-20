package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.AtlasElements
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.db.jooq.remocra.tables.pojos.AtlasDocument
import remocra.db.jooq.remocra.tables.references.ATLAS_ANNEXE
import remocra.db.jooq.remocra.tables.references.ATLAS_DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT

class AtlasRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getAllSeparated(): AtlasElements {
        return AtlasElements(
            atlasAnnexe = getAtlasAnnexes(),
            atlasDocument = getAtlasDocuments(),
        )
    }

    fun hasDocumentsOrAnnexes(): Boolean =
        dsl.fetchExists(
            dsl.selectOne().from(ATLAS_DOCUMENT)
                .unionAll(dsl.selectOne().from(ATLAS_ANNEXE)),
        )

    fun getAtlasAnnexes(): List<AtlasAnnexe> =
        dsl.selectFrom(ATLAS_ANNEXE)
            .where(ATLAS_ANNEXE.ACTIF.isTrue)
            .orderBy(ATLAS_ANNEXE.ORDER)
            .fetchInto()

    private fun getAtlasDocuments(): List<AtlasDocument> = dsl.selectFrom(ATLAS_DOCUMENT).fetchInto()

    fun deleteAll() {
        val documentIds =
            dsl.select(ATLAS_DOCUMENT.DOCUMENT_ID)
                .from(ATLAS_DOCUMENT)
                .union(
                    dsl.select(ATLAS_ANNEXE.DOCUMENT_ID)
                        .from(ATLAS_ANNEXE),
                )
                .fetch(ATLAS_DOCUMENT.DOCUMENT_ID)

        dsl.deleteFrom(ATLAS_DOCUMENT).execute()
        dsl.deleteFrom(ATLAS_ANNEXE).execute()

        if (documentIds.isNotEmpty()) {
            dsl.deleteFrom(DOCUMENT)
                .where(DOCUMENT.ID.`in`(documentIds))
                .execute()
        }
    }

    fun insertAtlasDocuments(atlas: AtlasDocument) {
        dsl.insertInto(ATLAS_DOCUMENT).set(dsl.newRecord(ATLAS_DOCUMENT, atlas)).execute()
    }

    fun insertAtlasAnnexes(atlas: AtlasAnnexe) {
        dsl.insertInto(ATLAS_ANNEXE).set(dsl.newRecord(ATLAS_ANNEXE, atlas)).execute()
    }

    fun findAtlasDocumentFileNames(): List<String> =
        dsl.select(DOCUMENT.NOM_FICHIER)
            .from(ATLAS_DOCUMENT)
            .join(DOCUMENT)
            .on(ATLAS_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .fetchInto()

    fun findAtlasAnnexeFileNames(): List<String> =
        dsl.select(DOCUMENT.NOM_FICHIER)
            .from(ATLAS_ANNEXE)
            .join(DOCUMENT)
            .on(ATLAS_ANNEXE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .fetchInto()

    fun resetAnnexesOrder() {
        dsl.update(ATLAS_ANNEXE)
            .set(ATLAS_ANNEXE.ORDER, null as Int?)
            .execute()
    }

    fun updateAnnexesOrder(updates: List<AtlasAnnexe>) {
        updates.forEach { annexe ->
            dsl.update(ATLAS_ANNEXE)
                .set(ATLAS_ANNEXE.ORDER, annexe.atlasAnnexeOrder)
                .set(ATLAS_ANNEXE.IS_VISIBLE, annexe.atlasAnnexeIsVisible)
                .where(ATLAS_ANNEXE.ID.eq(annexe.atlasAnnexeId))
                .execute()
        }
    }
}
