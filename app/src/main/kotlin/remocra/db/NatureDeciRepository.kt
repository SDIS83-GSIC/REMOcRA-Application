package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.NatureDeci
import remocra.db.jooq.tables.references.NATURE_DECI
import java.util.UUID

class NatureDeciRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<NatureDeci> {

    override fun getMapById(): Map<UUID, NatureDeci> = dsl.selectFrom(NATURE_DECI)
        // TODO quand on aura un flag actif.where(NATURE_DECI.ACTIF.isTrue)
        .fetchInto<NatureDeci>().associateBy { it.natureDeciId }
}
