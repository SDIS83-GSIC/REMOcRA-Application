package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.SortField
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypeZoneIntegration
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_Within
import java.util.UUID

class ZoneIntegrationRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getById(id: UUID): ZoneIntegration = dsl.selectFrom(ZONE_INTEGRATION)
        .where(ZONE_INTEGRATION.ID.eq(id)).fetchSingleInto()

    fun getAll(): Collection<ZoneIntegration> {
        return dsl.selectFrom(ZONE_INTEGRATION)
            .where(ZONE_INTEGRATION.ACTIF.isTrue)
            .and(ZONE_INTEGRATION.TYPE.eq(TypeZoneIntegration.ZONE_COMPETENCE))
            .orderBy(ZONE_INTEGRATION.LIBELLE)
            .fetchInto()
    }

    fun checkByOrganismeId(geometry: Field<Geometry?>, organismeId: UUID): Boolean? =
        dsl.select(ST_Within(geometry, ZONE_INTEGRATION.GEOMETRIE))
            .from(ZONE_INTEGRATION)
            .join(ORGANISME).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .where(ORGANISME.ID.eq(organismeId))
            .fetchOneInto()

    fun upsertZoneIntegration(zoneIntegration: ZoneIntegration): ZoneIntegration {
        val record = dsl.newRecord(ZONE_INTEGRATION, zoneIntegration)
        return dsl.insertInto(ZONE_INTEGRATION)
            .set(record)
            .onConflict(ZONE_INTEGRATION.CODE)
            .doUpdate()
            .set(ZONE_INTEGRATION.LIBELLE, zoneIntegration.zoneIntegrationLibelle)
            .set(ZONE_INTEGRATION.GEOMETRIE, zoneIntegration.zoneIntegrationGeometrie)
            .returning().fetchSingleInto()
    }

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<ZoneIntegration> =
        dsl
            .selectFrom(ZONE_INTEGRATION)
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() }
                    ?: listOf(ZONE_INTEGRATION.LIBELLE),
            )
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: ZoneIntegrationRepository.Filter?) =
        dsl.select(ZONE_INTEGRATION.ID)
            .from(ZONE_INTEGRATION)
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    fun updateZoneIntegration(zoneIntegrationId: UUID, zoneIntegrationCode: String, zoneIntegrationLibelle: String, zoneIntegrationActif: Boolean) =
        dsl.update(ZONE_INTEGRATION)
            .set(ZONE_INTEGRATION.CODE, zoneIntegrationCode)
            .set(ZONE_INTEGRATION.LIBELLE, zoneIntegrationLibelle)
            .set(ZONE_INTEGRATION.ACTIF, zoneIntegrationActif)
            .where(ZONE_INTEGRATION.ID.eq(zoneIntegrationId))
            .execute()

    data class Filter(
        val zoneIntegrationCode: String?,
        val zoneIntegrationLibelle: String?,
        val zoneIntegrationActif: Boolean?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    zoneIntegrationCode?.let { DSL.and(ZONE_INTEGRATION.CODE.containsIgnoreCase(it)) },
                    zoneIntegrationLibelle?.let { DSL.and(ZONE_INTEGRATION.LIBELLE.containsIgnoreCase(it)) },
                    zoneIntegrationActif?.let { DSL.and(ZONE_INTEGRATION.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val zoneIntegrationCode: Int?,
        val zoneIntegrationLibelle: Int?,
        val zoneIntegrationActif: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            ZONE_INTEGRATION.CODE.getSortField(zoneIntegrationCode),
            ZONE_INTEGRATION.LIBELLE.getSortField(zoneIntegrationLibelle),
        )
    }
}
