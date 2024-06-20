package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import java.util.UUID

class ModelePibiRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<ModelePibi> {

    override fun getMapById(): Map<UUID, ModelePibi> = dsl.selectFrom(MODELE_PIBI).where(MODELE_PIBI.ACTIF.isTrue).fetchInto<ModelePibi>().associateBy { it.modelePibiId }
}
