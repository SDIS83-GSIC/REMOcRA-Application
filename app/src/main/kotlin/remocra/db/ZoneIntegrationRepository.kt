package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.ZoneIntegration
import remocra.db.jooq.tables.references.ZONE_INTEGRATION
import java.util.UUID

class ZoneIntegrationRepository @Inject constructor(private val dsl: DSLContext) {

    fun getById(id: UUID): ZoneIntegration = dsl.selectFrom(ZONE_INTEGRATION)
        .where(ZONE_INTEGRATION.ID.eq(id)).fetchSingleInto()
}
