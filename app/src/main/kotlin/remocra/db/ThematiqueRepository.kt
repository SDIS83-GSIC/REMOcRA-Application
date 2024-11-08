package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.selectDistinct
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.BlocDocument
import remocra.db.jooq.remocra.tables.references.BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_PROFIL_DROIT_BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_BLOC_DOCUMENT
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import java.util.UUID

class ThematiqueRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(actif: Boolean? = null): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            THEMATIQUE.ID.`as`("id"),
            THEMATIQUE.CODE.`as`("code"),
            THEMATIQUE.LIBELLE.`as`("libelle"),
        )
            .from(THEMATIQUE)
            .where(actif?.let { THEMATIQUE.ACTIF.eq(it) } ?: DSL.noCondition())
            .orderBy(THEMATIQUE.LIBELLE)
            .fetchInto()

    fun getBlocDocumentWithThematique(
        listeThematiqueId: Collection<UUID>,
        limit: Int?,
        profilDroitId: UUID,
        params: Params<Filter, Sort>?,
    ): Collection<BlocDocument> =
        dsl
            .selectDistinct(
                BLOC_DOCUMENT.ID,
                BLOC_DOCUMENT.LIBELLE,
                BLOC_DOCUMENT.DATE_MAJ,
                BLOC_DOCUMENT.DESCRIPTION,
                DOCUMENT.ID,
            )
            .from(DOCUMENT)
            .join(BLOC_DOCUMENT)
            .on(BLOC_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_BLOC_DOCUMENT)
            .on(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .leftJoin(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .on(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .where(L_THEMATIQUE_BLOC_DOCUMENT.THEMATIQUE_ID.`in`(listeThematiqueId))
            .and(L_PROFIL_DROIT_BLOC_DOCUMENT.PROFIL_DROIT_ID.eq(profilDroitId))
            .and(params?.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                params?.sortBy?.toCondition()
                    .takeIf { !it.isNullOrEmpty() } ?: listOf(BLOC_DOCUMENT.DATE_MAJ.desc()),
            )
            .limit(params?.limit ?: limit)
            .fetchInto()

    fun countBlocDocumentWithThematique(
        listeThematiqueId: Collection<UUID>,
        profilDroitId: UUID,
        params: Params<Filter, Sort>?,
    ): Int =
        dsl
            .selectDistinct(
                BLOC_DOCUMENT.ID,
            )
            .from(DOCUMENT)
            .join(BLOC_DOCUMENT)
            .on(BLOC_DOCUMENT.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_BLOC_DOCUMENT)
            .on(L_THEMATIQUE_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .leftJoin(L_PROFIL_DROIT_BLOC_DOCUMENT)
            .on(L_PROFIL_DROIT_BLOC_DOCUMENT.BLOC_DOCUMENT_ID.eq(BLOC_DOCUMENT.ID))
            .where(L_THEMATIQUE_BLOC_DOCUMENT.THEMATIQUE_ID.`in`(listeThematiqueId))
            .and(L_PROFIL_DROIT_BLOC_DOCUMENT.PROFIL_DROIT_ID.eq(profilDroitId))
            .and(params?.filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val libelle: String?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    libelle?.let { DSL.and(BLOC_DOCUMENT.LIBELLE.containsIgnoreCase(it)) },
                ),
            )
    }

    data class Sort(
        val libelle: Int?,
        val date: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            BLOC_DOCUMENT.LIBELLE.getSortField(libelle),
            BLOC_DOCUMENT.DATE_MAJ.getSortField(date),
        )
    }
}
