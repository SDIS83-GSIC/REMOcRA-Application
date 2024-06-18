package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.references.NATURE
import java.util.UUID

class NatureRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Nature> {

    override fun getMapById(): Map<UUID, Nature> = dsl.selectFrom(NATURE).where(NATURE.ACTIF.isTrue).fetchInto<Nature>().associateBy { it.natureId }

    fun getNatureForSelect(): List<IdLibelleNature> =
        dsl.select(NATURE.ID, NATURE.LIBELLE)
            .from(NATURE)
            .orderBy(NATURE.LIBELLE)
            .fetchInto()

    data class IdLibelleNature(
        val natureId: UUID,
        val natureLibelle: String,
    )
}
