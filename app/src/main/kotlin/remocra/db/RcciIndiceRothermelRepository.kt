package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.RcciIndiceRothermel
import remocra.db.jooq.remocra.tables.references.RCCI_INDICE_ROTHERMEL
import java.util.UUID

class RcciIndiceRothermelRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<RcciIndiceRothermel>, AbstractRepository() {

    override fun getMapById(): Map<UUID, RcciIndiceRothermel> = dsl.selectFrom(RCCI_INDICE_ROTHERMEL)
        .orderBy(RCCI_INDICE_ROTHERMEL.LIBELLE).fetchInto<RcciIndiceRothermel>().associateBy { it.rcciIndiceRothermelId }
}
