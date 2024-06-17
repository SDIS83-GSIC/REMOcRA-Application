package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Materiau
import remocra.db.jooq.tables.references.MATERIAU
import java.util.UUID

class MateriauRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Materiau> {

    override fun getMapById(): Map<UUID, Materiau> = dsl.selectFrom(MATERIAU).where(MATERIAU.ACTIF.isTrue).fetchInto<Materiau>().associateBy { it.materiauId }
}
