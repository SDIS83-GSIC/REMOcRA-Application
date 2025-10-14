package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.references.DOMAINE
import java.util.UUID

class DomaineRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Domaine>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Domaine> = dsl.selectFrom(DOMAINE).where(DOMAINE.ACTIF.isTrue).orderBy(DOMAINE.LIBELLE).fetchInto<Domaine>().associateBy { it.domaineId }

    fun getFistDomaineId(): UUID =
        dsl.select(DOMAINE.ID)
            .from(DOMAINE)
            .limit(1)
            .fetchSingleInto()
}
