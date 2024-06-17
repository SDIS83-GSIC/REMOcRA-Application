package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Domaine
import remocra.db.jooq.tables.references.DOMAINE
import java.util.UUID

class DomaineRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Domaine> {

    override fun getMapById(): Map<UUID, Domaine> = dsl.selectFrom(DOMAINE).where(DOMAINE.ACTIF.isTrue).fetchInto<Domaine>().associateBy { it.domaineId }
}
