package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.TypeCanalisation
import remocra.db.jooq.remocra.tables.references.TYPE_CANALISATION
import java.util.UUID

class TypeCanalisationRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<TypeCanalisation>, AbstractRepository() {

    override fun getMapById(): Map<UUID, TypeCanalisation> = dsl.selectFrom(TYPE_CANALISATION).where(TYPE_CANALISATION.ACTIF.isTrue).orderBy(TYPE_CANALISATION.LIBELLE).fetchInto<TypeCanalisation>().associateBy { it.typeCanalisationId }
}
