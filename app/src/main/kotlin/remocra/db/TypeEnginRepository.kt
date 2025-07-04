package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.TypeEngin
import remocra.db.jooq.remocra.tables.references.TYPE_ENGIN
import java.util.UUID

class TypeEnginRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<TypeEngin>, AbstractRepository() {

    override fun getMapById(): Map<UUID, TypeEngin> =
        dsl.selectFrom(TYPE_ENGIN)
            .where(TYPE_ENGIN.ACTIF.isTrue)
            .orderBy(TYPE_ENGIN.LIBELLE)
            .fetchInto<TypeEngin>()
            .associateBy { it.typeEnginId }
}
