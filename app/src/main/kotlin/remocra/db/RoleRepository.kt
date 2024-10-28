package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.ROLE

class RoleRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(ROLE.ID.`as`("id"), ROLE.CODE.`as`("code"), ROLE.LIBELLE.`as`("libelle"))
            .from(ROLE)
            .where(ROLE.ACTIF.isTrue)
            .fetchInto()
}
