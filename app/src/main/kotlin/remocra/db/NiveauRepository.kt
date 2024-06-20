package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Niveau
import remocra.db.jooq.tables.references.NIVEAU
import java.util.UUID

class NiveauRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Niveau> {

    override fun getMapById(): Map<UUID, Niveau> = dsl.selectFrom(NIVEAU)
        .where(NIVEAU.ACTIF.isTrue)
        .fetchInto<Niveau>().associateBy { it.niveauId }
}
