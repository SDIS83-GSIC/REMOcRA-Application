package remocra.db

import jakarta.inject.Inject
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
import kotlin.math.absoluteValue

class SiteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
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
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
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
                    siteCode?.let { DSL.and(SITE.CODE.containsIgnoreCaseUnaccent(it)) },
                    siteLibelle?.let { DSL.and(SITE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
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
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            siteCode?.let { "siteCode" to it },
            siteLibelle?.let { "siteLibelle" to it },
            siteActif?.let { "siteActif" to it },
            gestionnaireLibelle?.let { "gestionnaireLibelle" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "siteCode" -> SITE.CODE.getSortField(pair.second)
                "siteLibelle" -> SITE.LIBELLE.getSortField(pair.second)
                "siteActif" -> SITE.ACTIF.getSortField(pair.second)
                "gestionnaireLibelle" -> GESTIONNAIRE.LIBELLE.getSortField(pair.second)
                else -> null
            }
        }
    }

    data class SiteWithGestionnaire(
        val siteId: UUID,
        val siteActif: Boolean,
        val siteCode: String,
        val siteLibelle: String,
        val siteGestionnaireId: UUID?,
        val gestionnaireLibelle: String?,
    )

    fun upsertSite(site: Site): Site {
        val record = dsl.newRecord(SITE, site)
        return dsl.insertInto(SITE)
            .set(record)
            .onConflict(SITE.CODE)
            .doUpdate()
            .set(SITE.LIBELLE, site.siteLibelle)
            .set(SITE.GEOMETRIE, site.siteGeometrie)
            .returning().fetchSingleInto()
    }

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
