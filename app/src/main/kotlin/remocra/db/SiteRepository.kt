package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Site
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.SITE
import java.util.UUID

class SiteRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): Collection<SiteWithGestionnaireId> =
        dsl.select(SITE.ID.`as`("id"), SITE.LIBELLE.`as`("libelle"), GESTIONNAIRE.ID)
            .from(SITE)
            .leftJoin(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(SITE.GESTIONNAIRE_ID))
            .where(SITE.ACTIF)
            .and(GESTIONNAIRE.ACTIF)
            .fetchInto()

    fun getAllSiteByGestionnaire(gestionnaireId: UUID): Collection<SiteWithGestionnaireId> =
        dsl.select(SITE.ID.`as`("id"), SITE.LIBELLE.`as`("libelle"), GESTIONNAIRE.ID)
            .from(SITE)
            .leftJoin(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(SITE.GESTIONNAIRE_ID))
            .where(SITE.ACTIF)
            .and(GESTIONNAIRE.ID.eq(gestionnaireId))
            .fetchInto()

    fun getById(siteId: UUID): Site =
        dsl.selectFrom(SITE).where(SITE.ID.eq(siteId)).fetchSingleInto()

    data class SiteWithGestionnaireId(
        val id: UUID,
        val libelle: String,
        val gestionnaireId: UUID,
    )
    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<SiteWithGestionnaire> =
        dsl
            .select(
                SITE.ID,
                SITE.ACTIF,
                SITE.CODE,
                SITE.LIBELLE,
                SITE.GESTIONNAIRE_ID,
                GESTIONNAIRE.LIBELLE,
            )
            .from(SITE)
            .leftJoin(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(SITE.GESTIONNAIRE_ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(SITE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.select(SITE.ID)
            .from(SITE)
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val siteCode: String?,
        val siteLibelle: String?,
        val siteActif: Boolean?,
        val siteGestionnaireId: UUID?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    siteCode?.let { DSL.and(SITE.CODE.contains(it)) },
                    siteLibelle?.let { DSL.and(SITE.LIBELLE.contains(it)) },
                    siteActif?.let { DSL.and(SITE.ACTIF.eq(it)) },
                    siteGestionnaireId?.let { DSL.and(SITE.GESTIONNAIRE_ID.eq(it)) },
                ),
            )
    }

    data class Sort(
        val siteCode: Int?,
        val siteLibelle: Int?,
        val siteActif: Int?,
        val gestionnaireLibelle: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            SITE.CODE.getSortField(siteCode),
            SITE.LIBELLE.getSortField(siteLibelle),
            GESTIONNAIRE.LIBELLE.getSortField(gestionnaireLibelle),
        )
    }

    data class SiteWithGestionnaire(
        val siteId: UUID,
        val siteActif: Boolean,
        val siteCode: String,
        val siteLibelle: String,
        val siteGestionnaireId: UUID?,
        val gestionnaireLibelle: String?,
    )

    fun updateSite(siteId: UUID, siteGestionnaireId: UUID?, siteCode: String, siteLibelle: String, siteActif: Boolean) =
        dsl.update(SITE)
            .set(SITE.GESTIONNAIRE_ID, siteGestionnaireId)
            .set(SITE.CODE, siteCode)
            .set(SITE.LIBELLE, siteLibelle)
            .set(SITE.ACTIF, siteActif)
            .where(SITE.ID.eq(siteId))
            .execute()

    fun deleteSite(siteId: UUID) =
        dsl.delete(SITE)
            .where(SITE.ID.eq(siteId))
            .execute()

    fun siteUsedInPei(siteId: UUID): Boolean =
        dsl.fetchExists(
            dsl.select(PEI.ID)
                .from(PEI)
                .where(PEI.SITE_ID.eq(siteId)),
        )
}
