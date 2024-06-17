package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Commune
import remocra.db.jooq.tables.references.COMMUNE
import java.util.UUID

class CommuneRepository @Inject constructor(private val dsl: DSLContext) {
    fun getMapById(): Map<UUID, Commune> = dsl.selectFrom(COMMUNE).fetchInto<Commune>().associateBy { it.communeId }
}
