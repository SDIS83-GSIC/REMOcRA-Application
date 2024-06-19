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

    fun setInactif(idUtilisateur: UUID) {
        setActif(false, idUtilisateur)
    }

    fun setActif(actif: Boolean, idUtilisateur: UUID) {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, actif)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .execute()
    }

    fun updateUtilisateur(idUtilisateur: UUID, nom: String, prenom: String, email: String, actif: Boolean) {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, actif)
            .set(UTILISATEUR.NOM, nom)
            .set(UTILISATEUR.PRENOM, prenom)
            .set(UTILISATEUR.EMAIL, email)
            .where(UTILISATEUR.ID.eq(idUtilisateur))
            .execute()
    }

    fun desactiveAllUsers() {
        dsl.update(UTILISATEUR)
            .set(UTILISATEUR.ACTIF, false)
            .execute()
    }

    fun deleteUtilisateurInactif(): Int =
        dsl.deleteFrom(UTILISATEUR)
            .where(UTILISATEUR.ACTIF.isFalse)
            .execute()

    fun insertUtilisateur(
        id: UUID,
        nom: String,
        prenom: String,
        email: String,
        username: String,
        actif: Boolean = true,
    ): Utilisateur =
        dsl.insertInto(UTILISATEUR)
            .set(UTILISATEUR.ID, id)
            .set(UTILISATEUR.ACTIF, actif)
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
        lastName: String,
        firstName: String,
        email: String,
        username: String,
    ): Utilisateur {
        // Cas nominal, l'utilisateur existe et son ID est le même que dans keycloak
        val userById = getUtilisateurById(id)

        if (userById != null) {
            // Si les propriétés ont changé, on les met à jour dans notre base
            if (userById.utilisateurNom != lastName || userById.utilisateurPrenom != firstName || userById.utilisateurEmail != email) {
                updateUtilisateur(
                    idUtilisateur = userById.utilisateurId,
                    nom = lastName,
                    prenom = firstName,
                    email = email,
                    actif = true,
                )
            }
            return userById
        }

        // L'utilisateur n'existe pas, on le crée
        return insertUtilisateur(id, lastName, firstName, email, username)
    }

    fun getAll(): Collection<Utilisateur> =
        dsl.selectFrom(UTILISATEUR).fetchInto()
}
