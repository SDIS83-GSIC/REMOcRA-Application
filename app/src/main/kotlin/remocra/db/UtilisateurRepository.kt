package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Utilisateur
import remocra.db.jooq.tables.references.UTILISATEUR
import java.util.UUID

class UtilisateurRepository @Inject constructor(private val dsl: DSLContext) {
    fun getUtilisateurById(idUtilisateur: UUID): Utilisateur? =
        dsl.selectFrom(UTILISATEUR)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .fetchOneInto()

    private fun insertUtilisateur(
        id: UUID,
        nom: String,
        prenom: String,
        email: String,
        username: String,
    ): Utilisateur =
        dsl.insertInto(UTILISATEUR)
            .set(UTILISATEUR.ID, id)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            .set(UTILISATEUR.USERNAME, username)
            .onConflict(UTILISATEUR.USERNAME)
            .doNothing()
            .returning()
            .fetchSingleInto()

    fun syncUtilisateur(
        id: UUID,
        familyName: String,
        firstName: String,
        email: String,
        username: String,
    ): Utilisateur {
        // Cas nominal, l'utilisateur existe et son ID est le même que dans keycloak
        val userById = getUtilisateurById(id)
        if (userById != null) {
            return userById
        }

        // L'utilisateur n'existe pas, on le crée
        return insertUtilisateur(id, familyName, firstName, email, username)
    }
}
