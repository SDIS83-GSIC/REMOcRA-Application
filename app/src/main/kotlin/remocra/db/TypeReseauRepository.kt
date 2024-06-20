package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.TypeReseau
import remocra.db.jooq.remocra.tables.references.TYPE_RESEAU
import java.util.UUID

class TypeReseauRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<TypeReseau> {

    override fun getMapById(): Map<UUID, TypeReseau> = dsl.selectFrom(TYPE_RESEAU).where(TYPE_RESEAU.ACTIF.isTrue).fetchInto<TypeReseau>().associateBy { it.typeReseauId }
}
