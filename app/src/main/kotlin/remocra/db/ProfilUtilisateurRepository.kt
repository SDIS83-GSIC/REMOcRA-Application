package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.ProfilUtilisateur
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import java.util.UUID

class ProfilUtilisateurRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            PROFIL_UTILISATEUR.ID.`as`("id"),
            PROFIL_UTILISATEUR.CODE.`as`("code"),
            PROFIL_UTILISATEUR.LIBELLE.`as`("libelle"),
        )
            .from(PROFIL_UTILISATEUR)
            .orderBy(PROFIL_UTILISATEUR.LIBELLE)
            .fetchInto()

    fun getAllActive(): Collection<ProfilUtilisateur> {
        return dsl.select(PROFIL_UTILISATEUR.fields().toList()).from(PROFIL_UTILISATEUR)
            .where(PROFIL_UTILISATEUR.ACTIF.isTrue).orderBy(PROFIL_UTILISATEUR.LIBELLE).fetchInto()
    }

    fun get(profilUtilisateurId: UUID): ProfilUtilisateur =
        dsl.selectFrom(PROFIL_UTILISATEUR).where(PROFIL_UTILISATEUR.ID.eq(profilUtilisateurId)).fetchSingleInto()
}
