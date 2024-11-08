package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.references.MARQUE_PIBI
import java.util.UUID

class MarquePibiRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<MarquePibi>, AbstractRepository() {

    override fun getMapById(): Map<UUID, MarquePibi> = dsl.selectFrom(MARQUE_PIBI).where(MARQUE_PIBI.ACTIF.isTrue).fetchInto<MarquePibi>().associateBy { it.marquePibiId }
}
