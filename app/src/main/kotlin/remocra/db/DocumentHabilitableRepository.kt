package remocra.db

import jakarta.inject.Inject
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
import remocra.db.jooq.remocra.tables.pojos.LGroupeFonctionnalitesDocumentHabilitable
import remocra.db.jooq.remocra.tables.pojos.LThematiqueDocumentHabilitable
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.math.absoluteValue

class DocumentHabilitableRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<DocumentHabilitableThematiqueGroupeFonctionnalites> =
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
                    selectDistinct(GROUPE_FONCTIONNALITES.LIBELLE)
                        .from(GROUPE_FONCTIONNALITES)
                        .join(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
                        .on(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
                        .where(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1()
                    }?.joinToString()
                }.`as`("listeGroupeFonctionnalites"),
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
            .on(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
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
            .leftJoin(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
            .on(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val documentHabilitableLibelle: String?,
        val listThematiqueId: List<UUID>?,
        val listGroupeFonctionnalitesId: List<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    documentHabilitableLibelle?.let { DSL.and(DOCUMENT_HABILITABLE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    listThematiqueId?.let { DSL.and(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.`in`(listThematiqueId)) },
                    listGroupeFonctionnalitesId?.let { DSL.and(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.GROUPE_FONCTIONNALITES_ID.`in`(listGroupeFonctionnalitesId)) },
                ),
            )
    }

    data class Sort(
        val documentHabilitableLibelle: Int?,
        val documentHabilitableDateMaj: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            documentHabilitableLibelle?.let { "documentHabilitableLibelle" to it },
            documentHabilitableDateMaj?.let { "documentHabilitableDateMaj" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "documentHabilitableLibelle" -> DOCUMENT_HABILITABLE.LIBELLE.getSortField(pair.second)
                "documentHabilitableDateMaj" -> DOCUMENT_HABILITABLE.DATE_MAJ.getSortField(pair.second)
                else -> null
            }
        }
    }

    data class DocumentHabilitableThematiqueGroupeFonctionnalites(
        val documentHabilitableId: UUID,
        val documentId: UUID,
        val documentHabilitableDateMaj: ZonedDateTime?,
        val documentHabilitableLibelle: String?,
        val documentHabilitableDescription: String?,
        val listeThematique: String?,
        val listeGroupeFonctionnalites: String?,
    )

    fun getDocumentByDocumentHabilitable(documentHabilitableId: UUID): Document? =
        dsl.select(DOCUMENT.fields().asList())
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .fetchOneInto()

    data class IdLibelleDate(
        val id: UUID,
        val libelle: String,
        val date: ZonedDateTime,
    )

    fun getDocumentIdLibelleDateByCodeThematique(codeThematique: String): Collection<IdLibelleDate> =
        dsl.select(DOCUMENT_HABILITABLE.ID.`as`("id"), DOCUMENT_HABILITABLE.LIBELLE.`as`("libelle"), DOCUMENT_HABILITABLE.DATE_MAJ.`as`("date"))
            .from(DOCUMENT_HABILITABLE)
            .join(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .join(THEMATIQUE)
            .on(THEMATIQUE.ID.eq(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID))
            .where(THEMATIQUE.CODE.eq(codeThematique))
            .and(THEMATIQUE.ACTIF.isTrue)
            .orderBy(DOCUMENT_HABILITABLE.DATE_MAJ)
            .fetchInto()

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

    fun insertGroupeFonctionnalitesDocumentHabilitable(LGroupeFonctionnalitesDocumentHabilitable: LGroupeFonctionnalitesDocumentHabilitable) =
        dsl.insertInto(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
            .set(dsl.newRecord(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE, LGroupeFonctionnalitesDocumentHabilitable))
            .execute()

    fun deleteDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(DOCUMENT_HABILITABLE)
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .execute()

    fun deleteThematiqueDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .where(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(documentHabilitableId))
            .execute()

    fun deleteGroupeFonctionnalitesDocumentHabilitable(documentHabilitableId: UUID) =
        dsl.deleteFrom(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
            .where(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(documentHabilitableId))
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
                    selectDistinct(GROUPE_FONCTIONNALITES.ID)
                        .from(GROUPE_FONCTIONNALITES)
                        .join(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE)
                        .on(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
                        .where(L_GROUPE_FONCTIONNALITES_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1() as UUID
                    }
                }.`as`("listeGroupeFonctionnalitesId"),
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(DOCUMENT_HABILITABLE.ID.eq(documentHabilitableId))
            .fetchSingleInto()
}
