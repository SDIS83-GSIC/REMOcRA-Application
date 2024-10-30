package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.BlocDocumentData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.BlocDocument
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LProfilDroitBlocDocument
import remocra.db.jooq.remocra.tables.pojos.LThematiqueBlocDocument
import remocra.db.jooq.remocra.tables.references.BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_PROFIL_DROIT_BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import java.time.ZonedDateTime
import java.util.UUID

class BlocDocumentRepository @Inject constructor(private val dsl: DSLContext) {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<BlocDocumentThematiqueProfilDroit> =
        dsl
            .selectDistinct(
                BLOC_DOCUMENT.ID,
                BLOC_DOCUMENT.LIBELLE,
                BLOC_DOCUMENT.DATE_MAJ,
                BLOC_DOCUMENT.DESCRIPTION,
                DOCUMENT.ID,
                multiset(
                    selectDistinct(THEMATIQUE.LIBELLE)
                        .from(THEMATIQUE)
                        .join(L_THEMATIQUE_BLOC_DOCUMENT)
                        .on(L_THEMATIQUE_BLOC_DOCUMENT.THEMATIQUE_ID.eq(THEMATIQUE.ID))
                        .where(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1()
                    }?.joinToString()
                }.`as`("listeThematique"),
                multiset(
                    selectDistinct(PROFIL_DROIT.LIBELLE)
                        .from(PROFIL_DROIT)
                        .join(L_PROFIL_DROIT_BLOC_DOCUMENT)
                        .on(L_PROFIL_DROIT_BLOC_DOCUMENT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                        .where(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1()
                    }?.joinToString()
                }.`as`("listeProfilDroit"),
            )
            .from(DOCUMENT)
            .join(BLOC_DOCUMENT)
            .on(BLOC_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_BLOC_DOCUMENT)
            .on(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .leftJoin(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .on(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() }
                    ?: listOf(BLOC_DOCUMENT.LIBELLE),
            )
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.selectDistinct(BLOC_DOCUMENT.ID)
            .from(BLOC_DOCUMENT)
            .leftJoin(L_THEMATIQUE_BLOC_DOCUMENT)
            .on(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .leftJoin(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .on(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val blocDocumentLibelle: String?,
        val listThematiqueId: List<UUID>?,
        val listProfilDroitId: List<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    blocDocumentLibelle?.let { DSL.and(BLOC_DOCUMENT.LIBELLE.containsIgnoreCase(it)) },
                    listThematiqueId?.let { DSL.and(L_THEMATIQUE_BLOC_DOCUMENT.THEMATIQUE_ID.`in`(listThematiqueId)) },
                    listProfilDroitId?.let { DSL.and(L_PROFIL_DROIT_BLOC_DOCUMENT.PROFIL_DROIT_ID.`in`(listProfilDroitId)) },
                ),
            )
    }

    data class Sort(
        val blocDocumentLibelle: Int?,
        val blocDocumentDateMaj: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            BLOC_DOCUMENT.LIBELLE.getSortField(blocDocumentLibelle),
            BLOC_DOCUMENT.DATE_MAJ.getSortField(blocDocumentDateMaj),
        )
    }

    data class BlocDocumentThematiqueProfilDroit(
        val blocDocumentId: UUID,
        val documentId: UUID,
        val blocDocumentDateMaj: ZonedDateTime?,
        val blocDocumentLibelle: String?,
        val blocDocumentDescription: String?,
        val listeThematique: String?,
        val listeProfilDroit: String?,
    )

    fun getDocumentByBlocDocument(blocDocumentId: UUID): Document? =
        dsl.select(DOCUMENT.fields().asList())
            .from(DOCUMENT)
            .join(BLOC_DOCUMENT)
            .on(BLOC_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(BLOC_DOCUMENT.ID.eq(blocDocumentId))
            .fetchOneInto()

    fun insertBlocDocument(blocDocument: BlocDocument) =
        dsl.insertInto(BLOC_DOCUMENT)
            .set(dsl.newRecord(BLOC_DOCUMENT, blocDocument))
            .execute()

    fun insertThematiqueBlocDocument(lThematiqueBlocDocument: LThematiqueBlocDocument) =
        dsl.insertInto(L_THEMATIQUE_BLOC_DOCUMENT)
            .set(dsl.newRecord(L_THEMATIQUE_BLOC_DOCUMENT, lThematiqueBlocDocument))
            .execute()

    fun insertProfilDroitBlocDocument(LProfilDroitBlocDocument: LProfilDroitBlocDocument) =
        dsl.insertInto(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .set(dsl.newRecord(L_PROFIL_DROIT_BLOC_DOCUMENT, LProfilDroitBlocDocument))
            .execute()

    fun deleteBlocDocument(blocDocumentId: UUID) =
        dsl.deleteFrom(BLOC_DOCUMENT)
            .where(BLOC_DOCUMENT.ID.eq(blocDocumentId))
            .execute()

    fun deleteThematiqueBlocDocument(blocDocumentId: UUID) =
        dsl.deleteFrom(L_THEMATIQUE_BLOC_DOCUMENT)
            .where(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(blocDocumentId))
            .execute()

    fun deleteProfilDroitBlocDocument(blocDocumentId: UUID) =
        dsl.deleteFrom(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .where(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(blocDocumentId))
            .execute()

    fun getById(blocDocumentId: UUID): BlocDocumentData =
        dsl
            .selectDistinct(
                BLOC_DOCUMENT.ID,
                BLOC_DOCUMENT.LIBELLE,
                BLOC_DOCUMENT.DESCRIPTION,
                multiset(
                    selectDistinct(THEMATIQUE.ID)
                        .from(THEMATIQUE)
                        .join(L_THEMATIQUE_BLOC_DOCUMENT)
                        .on(L_THEMATIQUE_BLOC_DOCUMENT.THEMATIQUE_ID.eq(THEMATIQUE.ID))
                        .where(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1() as UUID
                    }
                }.`as`("listeThematiqueId"),
                multiset(
                    selectDistinct(PROFIL_DROIT.ID)
                        .from(PROFIL_DROIT)
                        .join(L_PROFIL_DROIT_BLOC_DOCUMENT)
                        .on(L_PROFIL_DROIT_BLOC_DOCUMENT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                        .where(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID)),
                ).convertFrom { record ->
                    record?.map { r ->
                        r.value1() as UUID
                    }
                }.`as`("listeProfilDroitId"),
            )
            .from(DOCUMENT)
            .join(BLOC_DOCUMENT)
            .on(BLOC_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(BLOC_DOCUMENT.ID.eq(blocDocumentId))
            .fetchSingleInto()
}
