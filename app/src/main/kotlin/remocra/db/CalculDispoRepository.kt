package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import java.util.UUID

class CalculDispoRepository@Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun deleteAnomalies(peiId: UUID, listAnomalieIds: Collection<UUID>) {
        dsl.deleteFrom(L_PEI_ANOMALIE).where(L_PEI_ANOMALIE.PEI_ID.eq(peiId)).and(L_PEI_ANOMALIE.ANOMALIE_ID.`in`(listAnomalieIds)).execute()
    }

    fun insertAnomaliesSysteme(peiId: UUID, listAnomalieIds: Collection<UUID>) {
        listAnomalieIds.forEach { anomalieId ->
            dsl.insertInto(L_PEI_ANOMALIE)
                .set(dsl.newRecord(L_PEI_ANOMALIE, LPeiAnomalie(peiId, anomalieId))).execute()
        }
    }
}
