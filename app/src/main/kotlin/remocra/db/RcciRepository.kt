package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.Rcci
import remocra.db.jooq.remocra.tables.pojos.RcciDocument
import remocra.db.jooq.remocra.tables.pojos.RcciTypeDegreCertitude
import remocra.db.jooq.remocra.tables.pojos.RcciTypeOrigineAlerte
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeCategorie
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeFamille
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheePartition
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.RCCI
import remocra.db.jooq.remocra.tables.references.RCCI_DOCUMENT
import remocra.db.jooq.remocra.tables.references.RCCI_TYPE_DEGRE_CERTITUDE
import remocra.db.jooq.remocra.tables.references.RCCI_TYPE_ORIGINE_ALERTE
import remocra.db.jooq.remocra.tables.references.RCCI_TYPE_PROMETHEE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.RCCI_TYPE_PROMETHEE_FAMILLE
import remocra.db.jooq.remocra.tables.references.RCCI_TYPE_PROMETHEE_PARTITION
import java.util.UUID

class RcciRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun selectRcci(rcciId: UUID): Rcci =
        dsl.selectFrom(RCCI).where(RCCI.ID.eq(rcciId)).fetchSingleInto()

    fun insertRcci(rcci: Rcci): Int =
        dsl.insertInto(RCCI).set(dsl.newRecord(RCCI, rcci)).execute()

    fun updateRcci(rcci: Rcci): Int =
        dsl.update(RCCI).set(dsl.newRecord(RCCI, rcci)).where(RCCI.ID.eq(rcci.rcciId)).execute()

    fun updateGeometry(rcciId: UUID, rcciGeometrie: Geometry): Int =
        dsl.update(RCCI).set(RCCI.GEOMETRIE, rcciGeometrie).where(RCCI.ID.eq(rcciId)).execute()

    fun deleteRcci(rcciId: UUID): Int =
        dsl.deleteFrom(RCCI).where(RCCI.ID.eq(rcciId)).execute()

    fun insertDocument(id: UUID, rcciId: UUID, documentId: UUID): Int =
        dsl.insertInto(RCCI_DOCUMENT)
            .set(RCCI_DOCUMENT.ID, id)
            .set(RCCI_DOCUMENT.RCCI_ID, rcciId)
            .set(RCCI_DOCUMENT.DOCUMENT_ID, documentId)
            .execute()

    fun selectDocument(rcciId: UUID): List<Document> =
        dsl.select(*DOCUMENT.fields()).from(DOCUMENT).join(RCCI_DOCUMENT).on(RCCI_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID)).where(RCCI_DOCUMENT.RCCI_ID.eq(rcciId)).fetchInto()

    fun selectMissingDocument(rcciId: UUID, toKeep: List<UUID>? = listOf()): List<RcciDocument> =
        dsl.selectFrom(RCCI_DOCUMENT).where(RCCI_DOCUMENT.RCCI_ID.eq(rcciId)).and(RCCI_DOCUMENT.DOCUMENT_ID.notIn(toKeep)).fetchInto()

    fun deleteMissingDocument(toDelete: List<UUID>?): Int =
        dsl.deleteFrom(RCCI_DOCUMENT).where(RCCI_DOCUMENT.DOCUMENT_ID.`in`(toDelete)).execute()

    fun deleteDocument(rcciId: UUID): Int =
        dsl.deleteFrom(RCCI_DOCUMENT).where(RCCI_DOCUMENT.RCCI_ID.eq(rcciId)).execute()

    /**
     * REFERENTIEL
     */
    fun getMapTypePrometheeFamille(): Map<UUID, RcciTypePrometheeFamille> =
        dsl.selectFrom(RCCI_TYPE_PROMETHEE_FAMILLE).orderBy(RCCI_TYPE_PROMETHEE_FAMILLE.LIBELLE).fetchInto<RcciTypePrometheeFamille>().associateBy { it.rcciTypePrometheeFamilleId }

    fun getMapTypePrometheePartition(): Map<UUID, RcciTypePrometheePartition> =
        dsl.selectFrom(RCCI_TYPE_PROMETHEE_PARTITION).orderBy(RCCI_TYPE_PROMETHEE_PARTITION.LIBELLE).fetchInto<RcciTypePrometheePartition>().associateBy { it.rcciTypePrometheePartitionId }

    fun getMapTypePrometheeCategorie(): Map<UUID, RcciTypePrometheeCategorie> =
        dsl.selectFrom(RCCI_TYPE_PROMETHEE_CATEGORIE).orderBy(RCCI_TYPE_PROMETHEE_CATEGORIE.LIBELLE).fetchInto<RcciTypePrometheeCategorie>().associateBy { it.rcciTypePrometheeCategorieId }

    fun getMapTypeOrigineAlerte(): Map<UUID, RcciTypeOrigineAlerte> =
        dsl.selectFrom(RCCI_TYPE_ORIGINE_ALERTE).orderBy(RCCI_TYPE_ORIGINE_ALERTE.LIBELLE).fetchInto<RcciTypeOrigineAlerte>().associateBy { it.rcciTypeOrigineAlerteId }

    fun getMapTypeDegreCertitude(): Map<UUID, RcciTypeDegreCertitude> =
        dsl.selectFrom(RCCI_TYPE_DEGRE_CERTITUDE).orderBy(RCCI_TYPE_DEGRE_CERTITUDE.LIBELLE).fetchInto<RcciTypeDegreCertitude>().associateBy { it.rcciTypeDegreCertitudeId }
}
