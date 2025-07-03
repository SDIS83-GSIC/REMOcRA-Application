package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import java.util.UUID

class NatureDeciRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<NatureDeci>, AbstractRepository() {

    fun getNatureDeciForSelect(): List<IdLibelleNatureDeci> =
        dsl.select(NATURE_DECI.ID, NATURE_DECI.LIBELLE)
            .from(NATURE_DECI)
            .orderBy(NATURE_DECI.LIBELLE)
            .fetchInto()

    data class IdLibelleNatureDeci(
        val natureDeciId: UUID,
        val natureDeciLibelle: String,
    )
    override fun getMapById(): Map<UUID, NatureDeci> = dsl.selectFrom(NATURE_DECI).where(NATURE_DECI.ACTIF.isTrue).orderBy(NATURE_DECI.LIBELLE)
        .fetchInto<NatureDeci>().associateBy { it.natureDeciId }
}
