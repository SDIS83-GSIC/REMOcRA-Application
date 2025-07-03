package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Materiau
import remocra.db.jooq.remocra.tables.references.MATERIAU
import java.util.UUID

class MateriauRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Materiau>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Materiau> = dsl.selectFrom(MATERIAU).where(MATERIAU.ACTIF.isTrue).orderBy(MATERIAU.LIBELLE).fetchInto<Materiau>().associateBy { it.materiauId }
}
