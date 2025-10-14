package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.FonctionContact
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT

class FonctionContactRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(FONCTION_CONTACT.ID.`as`("id"), FONCTION_CONTACT.CODE.`as`("code"), FONCTION_CONTACT.LIBELLE.`as`("libelle"))
            .from(FONCTION_CONTACT)
            .where(FONCTION_CONTACT.ACTIF.isTrue)
            .fetchInto()

    fun getAllForMobile(): Collection<FonctionContact> =
        dsl.selectFrom(FONCTION_CONTACT)
            .fetchInto()
}
