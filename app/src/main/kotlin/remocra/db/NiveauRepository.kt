package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Niveau
import remocra.db.jooq.remocra.tables.references.NIVEAU
import java.util.UUID

class NiveauRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Niveau>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Niveau> = dsl.selectFrom(NIVEAU)
        .where(NIVEAU.ACTIF.isTrue)
        .orderBy(NIVEAU.LIBELLE)
        .fetchInto<Niveau>().associateBy { it.niveauId }
}
