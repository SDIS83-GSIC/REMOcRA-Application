package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.TypeOrganisme
import remocra.db.jooq.tables.references.TYPE_ORGANISME

class TypeOrganismeRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(limit: Int?, offset: Int?): Collection<TypeOrganisme> =
        dsl.selectFrom(TYPE_ORGANISME)
            .where(TYPE_ORGANISME.ACTIF.isTrue)
            .orderBy(TYPE_ORGANISME.CODE)
            .limit(limit)
            .offset(offset)
            .fetchInto()
}
