package remocra.auth

import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import java.security.Principal
import java.util.UUID

class
UserInfo : KeycloakOidcProfile() {
    lateinit var utilisateur: Utilisateur

    lateinit var droits: Collection<Droit>

    var zoneCompetence: ZoneIntegration? = null

    val utilisateurId: UUID
        get() = UUID.fromString(subject)

    val prenom: String
        get() = firstName

    val nom: String
        get() = familyName

    val organismeId: UUID?
        get() = utilisateur.utilisateurOrganismeId

    // Les organismes affiliés, à savoir l'organisme de rattachement et ses enfants, pour simplifier les requêtes hiérarchiques
    lateinit var affiliatedOrganismeIds: Set<UUID>

    val isActif: Boolean
        get() = !this.roles.contains("inactif")

    val isSuperAdmin: Boolean
        get() = utilisateur.utilisateurIsSuperAdmin ?: false

    override fun asPrincipal(): Principal {
        return UserPrincipal(this)
    }

    fun asJavascriptUserProfile(): JavascriptUserProfile {
        return JavascriptUserProfile(
            utilisateurId = this.utilisateurId,
            nom = this.familyName,
            prenom = this.firstName,
            username = this.username,
            organismeId = this.organismeId,
            droits = this.droits,
            isSuperAdmin = this.isSuperAdmin,
        )
    }

    /**
     * Classe représentant les habilitations de l'utilisateur, ayant pour vocation à être sérialisée
     * puis passée au javascript pour utilisation directe.
     */
    class JavascriptUserProfile(
        val utilisateurId: UUID,
        val nom: String,
        val prenom: String,
        val username: String,
        val organismeId: UUID?,
        val droits: Collection<Droit>,
        val isSuperAdmin: Boolean = false,
    )
}
