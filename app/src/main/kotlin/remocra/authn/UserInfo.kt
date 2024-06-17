package remocra.authn

import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile
import java.security.Principal
import java.util.UUID

class UserInfo : KeycloakOidcProfile() {
    val idUtilisateur: UUID
        get() = UUID.fromString(subject)

    val prenom: String
        get() = firstName

    val nom: String
        get() = familyName

    override fun asPrincipal(): Principal {
        return UserPrincipal(this)
    }

    fun asJavascriptUserProfile(): JavascriptUserProfile {
        return JavascriptUserProfile(
            idUtilisateur = this.idUtilisateur,
            nom = this.familyName,
            prenom = this.firstName,
            username = this.username,
        )
    }

    /**
     * Classe représentant les habilitations de l'utilisateur, ayant pour vocation à être sérialisée
     * puis passée au javascript pour utilisation directe.
     */
    class JavascriptUserProfile(
        val idUtilisateur: UUID,
        val nom: String,
        val prenom: String,
        val username: String,
    )
}
