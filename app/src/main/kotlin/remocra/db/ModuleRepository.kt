package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Module
import remocra.db.jooq.remocra.tables.references.MODULE

class ModuleRepository @Inject constructor(private val dsl: DSLContext) {
    fun getModules(): Collection<Module> =
        dsl.selectFrom(MODULE)
            .orderBy(MODULE.COLONNE, MODULE.LIGNE).fetchInto()
}
