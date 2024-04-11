package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.Utilisateur
import remocra.db.jooq.tables.references.UTILISATEUR
import java.util.UUID

class UtilisateurRepository @Inject constructor(private val dsl: DSLContext) {
    fun getUtilisateurById(idUtilisateur: UUID): Utilisateur? =
        dsl.selectFrom(UTILISATEUR)
            .where(UTILISATEUR.UTILISATEUR_ID.eq(idUtilisateur))
            .fetchOneInto()

    private fun insertUtilisateur(
        id: UUID,
        nom: String,
        prenom: String,
        email: String,
        username: String,
    ): Utilisateur =
        dsl.insertInto(UTILISATEUR)
            .set(UTILISATEUR.UTILISATEUR_ID, id)
            .set(UTILISATEUR.UTILISATEUR_NOM, nom)
            .set(UTILISATEUR.UTILISATEUR_PRENOM, prenom)
            .set(UTILISATEUR.UTILISATEUR_EMAIL, email)
            .set(UTILISATEUR.UTILISATEUR_USERNAME, username)
            .onConflict(UTILISATEUR.UTILISATEUR_USERNAME)
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
