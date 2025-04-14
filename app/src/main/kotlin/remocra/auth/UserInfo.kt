package remocra.auth

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import java.io.Serializable
import java.util.UUID

class UserInfo(
    val utilisateur: Utilisateur,
    val droits: Set<Droit>,
    val zoneCompetence: ZoneIntegration?,
    val affiliatedOrganismeIds: Set<UUID>,
    val profilDroits: ProfilDroit?,
) : Serializable {
    val username: String
        get() = utilisateur.utilisateurUsername

    val utilisateurId: UUID
        get() = utilisateur.utilisateurId

    val prenom: String
        get() = utilisateur.utilisateurPrenom

    val nom: String
        get() = utilisateur.utilisateurNom

    val email: String
        get() = utilisateur.utilisateurEmail

    val organismeId: UUID?
        get() = utilisateur.utilisateurOrganismeId

    val isActif: Boolean
        get() = utilisateur.utilisateurActif

    val isSuperAdmin: Boolean
        get() = utilisateur.utilisateurIsSuperAdmin ?: false

    fun asJavascriptUserProfile(): JavascriptUserProfile {
        return JavascriptUserProfile(
            utilisateurId = utilisateurId,
            nom = nom,
            prenom = prenom,
            username = username,
            organismeId = organismeId,
            zoneIntegrationExtent = zoneCompetence?.zoneIntegrationGeometrie,
            droits = this.droits,
            isSuperAdmin = isSuperAdmin,
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
        val zoneIntegrationExtent: Geometry?,
        val droits: Collection<Droit>,
        val isSuperAdmin: Boolean = false,
    )
}
