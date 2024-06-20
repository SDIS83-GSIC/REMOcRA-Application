package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.references.PARAMETRE

class ParametreRepository @Inject constructor(private val dsl: DSLContext) {

    fun getMapParametres() = dsl.selectFrom(PARAMETRE).fetchInto<Parametre>().associateBy { it.parametreCode }
}
