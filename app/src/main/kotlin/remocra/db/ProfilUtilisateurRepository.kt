package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR

class ProfilUtilisateurRepository @Inject constructor(private val dsl: DSLContext) {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            PROFIL_UTILISATEUR.ID.`as`("id"),
            PROFIL_UTILISATEUR.CODE.`as`("code"),
            PROFIL_UTILISATEUR.LIBELLE.`as`("libelle"),
        )
            .from(PROFIL_UTILISATEUR)
            .orderBy(PROFIL_UTILISATEUR.LIBELLE)
            .fetchInto()
}
