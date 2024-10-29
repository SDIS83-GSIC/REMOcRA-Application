package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT

class ProfilDroitRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            PROFIL_DROIT.ID.`as`("id"),
            PROFIL_DROIT.CODE.`as`("code"),
            PROFIL_DROIT.LIBELLE.`as`("libelle"),
        )
            .from(PROFIL_DROIT)
            .orderBy(PROFIL_DROIT.LIBELLE)
            .fetchInto()
}
