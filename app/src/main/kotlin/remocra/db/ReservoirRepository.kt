package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Reservoir
import remocra.db.jooq.remocra.tables.references.RESERVOIR
import java.util.UUID

class ReservoirRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Reservoir>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Reservoir> = dsl.selectFrom(RESERVOIR).where(RESERVOIR.ACTIF.isTrue).orderBy(RESERVOIR.LIBELLE).fetchInto<Reservoir>().associateBy { it.reservoirId }
}
