package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import java.util.UUID

class PoidsAnomalieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getPoidsAnomalies(anomaliesIds: Collection<UUID>, natureId: UUID, typeVisite: TypeVisite?): Collection<PoidsAnomalie> =
        dsl.selectFrom(POIDS_ANOMALIE)
            .where(POIDS_ANOMALIE.ANOMALIE_ID.`in`(anomaliesIds))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(natureId))
            .and(typeVisite?.let { POIDS_ANOMALIE.TYPE_VISITE.contains(typeVisite) } ?: DSL.noCondition())
            .fetchInto()
}
