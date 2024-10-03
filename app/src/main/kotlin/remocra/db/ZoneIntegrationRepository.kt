package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Geometry
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
}
