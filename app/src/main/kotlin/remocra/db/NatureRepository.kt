package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Nature
import remocra.db.jooq.tables.references.NATURE
import java.util.UUID

class NatureRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Nature> {

    override fun getMapById(): Map<UUID, Nature> = dsl.selectFrom(NATURE).where(NATURE.ACTIF.isTrue).fetchInto<Nature>().associateBy { it.natureId }
}
