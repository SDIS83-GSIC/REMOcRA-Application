package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Diametre
import remocra.db.jooq.tables.references.DIAMETRE
import java.util.UUID

class DiametreRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Diametre> {

    override fun getMapById(): Map<UUID, Diametre> = dsl.selectFrom(DIAMETRE).where(DIAMETRE.ACTIF.isTrue).fetchInto<Diametre>().associateBy { it.diametreId }
}
