package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.DocumentHabilitableData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.DocumentHabilitable
import remocra.db.jooq.remocra.tables.pojos.LProfilDroitDocumentHabilitable
import remocra.db.jooq.remocra.tables.pojos.LThematiqueDocumentHabilitable
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_PROFIL_DROIT_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import java.time.ZonedDateTime
import java.util.UUID

class DocumentHabilitableRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<DocumentHabilitableThematiqueProfilDroit> =
        dsl
            .selectDistinct(
                DOCUMENT_HABILITABLE.ID,
                DOCUMENT_HABILITABLE.LIBELLE,
                DOCUMENT_HABILITABLE.DATE_MAJ,
                DOCUMENT_HABILITABLE.DESCRIPTION,
                DOCUMENT.ID,
                multiset(
                    selectDistinct(THEMATIQUE.LIBELLE)
                        .from(THEMATIQUE)
                        .join(L_THEMATIQUE_DOCUMENT_HABILITABLE)
                        .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.eq(THEMATIQUE.ID))
                        .where(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1()
                    }?.joinToString()
                }.`as`("listeThematique"),
                multiset(
                    selectDistinct(PROFIL_DROIT.LIBELLE)
                        .from(PROFIL_DROIT)
                        .join(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
                        .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                        .where(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1()
                    }?.joinToString()
                }.`as`("listeProfilDroit"),
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() }
                    ?: listOf(DOCUMENT_HABILITABLE.LIBELLE),
            )
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.selectDistinct(DOCUMENT_HABILITABLE.ID)
            .from(DOCUMENT_HABILITABLE)
            .leftJoin(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val documentHabilitableLibelle: String?,
        val listThematiqueId: List<UUID>?,
        val listProfilDroitId: List<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    documentHabilitableLibelle?.let { DSL.and(DOCUMENT_HABILITABLE.LIBELLE.containsIgnoreCase(it)) },
                    listThematiqueId?.let { DSL.and(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.`in`(listThematiqueId)) },
                    listProfilDroitId?.let { DSL.and(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.PROFIL_DROIT_ID.`in`(listProfilDroitId)) },
                ),
            )
    }

    data class Sort(
        val documentHabilitableLibelle: Int?,
        val documentHabilitableDateMaj: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            DOCUMENT_HABILITABLE.LIBELLE.getSortField(documentHabilitableLibelle),
            DOCUMENT_HABILITABLE.DATE_MAJ.getSortField(documentHabilitableDateMaj),
        )
    }

    data class DocumentHabilitableThematiqueProfilDroit(
        val documentHabilitableId: UUID,
        val documentId: UUID,
        val documentHabilitableDateMaj: ZonedDateTime?,
        val documentHabilitableLibelle: String?,
        val documentHabilitableDescription: String?,
        val listeThematique: String?,
        val listeProfilDroit: String?,
    )

    fun getDocumentByDocumentHabilitable(documentHabilitableId: UUID): Document? =
        dsl.select(DOCUMENT.fields().asList())
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .fetchOneInto()

    fun insertDocumentHabilitable(documentHabilitable: DocumentHabilitable) =
        dsl.insertInto(DOCUMENT_HABILITABLE)
            .set(dsl.newRecord(DOCUMENT_HABILITABLE, documentHabilitable))
            .execute()

    fun updateDocumentHabilitable(documentHabilitable: DocumentHabilitable) =
        dsl.update(DOCUMENT_HABILITABLE)
            .set(DOCUMENT_HABILITABLE.LIBELLE, documentHabilitable.documentHabilitableLibelle)
            .set(DOCUMENT_HABILITABLE.DATE_MAJ, documentHabilitable.documentHabilitableDateMaj)
            .set(DOCUMENT_HABILITABLE.DESCRIPTION, documentHabilitable.documentHabilitableDescription)
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitable.documentHabilitableId))
            .execute()

    fun insertThematiqueDocumentHabilitable(lThematiqueDocumentHabilitable: LThematiqueDocumentHabilitable) =
        dsl.insertInto(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .set(dsl.newRecord(L_THEMATIQUE_DOCUMENT_HABILITABLE, lThematiqueDocumentHabilitable))
            .execute()

    fun insertProfilDroitDocumentHabilitable(LProfilDroitDocumentHabilitable: LProfilDroitDocumentHabilitable) =
        dsl.insertInto(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .set(dsl.newRecord(L_PROFIL_DROIT_DOCUMENT_HABILITABLE, LProfilDroitDocumentHabilitable))
            .execute()

    fun deleteDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(DOCUMENT_HABILITABLE)
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .execute()

    fun deleteThematiqueDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .where(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(documentHabilitableId))
            .execute()

    fun deleteProfilDroitDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .where(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(documentHabilitableId))
            .execute()

    fun getById(documentHabilitableId: UUID): DocumentHabilitableData =
        dsl
            .selectDistinct(
                DOCUMENT_HABILITABLE.ID,
                DOCUMENT_HABILITABLE.LIBELLE,
                DOCUMENT_HABILITABLE.DESCRIPTION,
                multiset(
                    selectDistinct(THEMATIQUE.ID)
                        .from(THEMATIQUE)
                        .join(L_THEMATIQUE_DOCUMENT_HABILITABLE)
                        .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.eq(THEMATIQUE.ID))
                        .where(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1() as UUID
                    }
                }.`as`("listeThematiqueId"),
                multiset(
                    selectDistinct(PROFIL_DROIT.ID)
                        .from(PROFIL_DROIT)
                        .join(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
                        .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                        .where(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1() as UUID
                    }
                }.`as`("listeProfilDroitId"),
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .fetchSingleInto()
}
