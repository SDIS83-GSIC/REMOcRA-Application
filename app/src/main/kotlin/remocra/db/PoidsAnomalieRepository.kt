package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import java.util.UUID

class PoidsAnomalieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getPoidsAnomalies(anomaliesIds: Collection<UUID>, natureId: UUID): Collection<PoidsAnomalie> =
        dsl.select(POIDS_ANOMALIE.fields().toList())
            .from(POIDS_ANOMALIE)
            .where(POIDS_ANOMALIE.ANOMALIE_ID.`in`(anomaliesIds))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(natureId))
            .fetchInto()
}
