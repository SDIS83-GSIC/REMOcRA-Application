package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME

class OrganismeRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(codeTypeOrganisme: String?, limit: Int?, offset: Int?): Collection<Organisme> =
        dsl.select(*ORGANISME.fields())
            .from(ORGANISME)
            .innerJoin(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ACTIF.isTrue)
            .and(TYPE_ORGANISME.ACTIF.isTrue)
            .and(codeTypeOrganisme?.let { TYPE_ORGANISME.CODE.eq(codeTypeOrganisme) })
            .limit(limit).offset(offset)
            .fetchInto()
}
