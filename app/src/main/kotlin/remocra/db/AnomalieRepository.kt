package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import java.util.UUID

class AnomalieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Anomalie> {

    override fun getMapById(): Map<UUID, Anomalie> = dsl.selectFrom(ANOMALIE).where(ANOMALIE.ACTIF.isTrue).fetchInto<Anomalie>().associateBy { it.anomalieId }
}
