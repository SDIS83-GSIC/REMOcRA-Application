package remocra.data

import java.net.URI
import java.util.UUID

data class UtilisateurData(
    val utilisateurId: UUID,
    val utilisateurActif: Boolean,
    val utilisateurEmail: String,
    val utilisateurNom: String,
    val utilisateurPrenom: String,
    val utilisateurUsername: String,
    val utilisateurTelephone: String?,
    val utilisateurCanBeNotified: Boolean?,
    val utilisateurProfilUtilisateurId: UUID?,
    val utilisateurOrganismeId: UUID?,
    val utilisateurIsSuperAdmin: Boolean = false,
    val uri: URI? = null,
)
