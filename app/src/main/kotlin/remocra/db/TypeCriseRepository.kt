package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE
import java.util.UUID

class TypeCriseRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<TypeCrise>, AbstractRepository() {

    override fun getMapById(): Map<UUID, TypeCrise> = dsl.selectFrom(TYPE_CRISE).where(TYPE_CRISE.ACTIF.isTrue).orderBy(TYPE_CRISE.LIBELLE).fetchInto<TypeCrise>().associateBy { it.typeCriseId }
}
