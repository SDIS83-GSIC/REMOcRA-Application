package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.RoleContact
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT

class RoleRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(ROLE_CONTACT.ID.`as`("id"), ROLE_CONTACT.CODE.`as`("code"), ROLE_CONTACT.LIBELLE.`as`("libelle"))
            .from(ROLE_CONTACT)
            .where(ROLE_CONTACT.ACTIF.isTrue)
            .fetchInto()

    fun getAllForMobile(): Collection<RoleContact> =
        dsl.selectFrom(ROLE_CONTACT)
            .fetchInto()
}
