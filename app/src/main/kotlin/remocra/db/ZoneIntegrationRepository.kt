package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.TypeZoneIntegration
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
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
}
