package remocra.data

import java.util.UUID

data class UtilisateurData(
    val utilisateurId: UUID,
    val utilisateurActif: Boolean,
    val utilisateurEmail: String,
    val utilisateurNom: String?,
    val utilisateurPrenom: String?,
    val utilisateurUsername: String,
    val utilisateurTelephone: String?,
    val utilisateurCanBeNotified: Boolean?,
    val utilisateurProfilUtilisateurId: UUID?,
    val utilisateurOrganismeId: UUID?,
    val utilisateurIsSuperAdmin: Boolean = false,
)

class LigneImportUtilisateur {
    var mail: String? = null
    var identifiant: String? = null
    var telephone: String? = null
    var nom: String? = null
    var prenom: String? = null
    var organisme: String? = null
    var profil_utilisateur: String? = null
    var actif: Boolean? = null
    var notifie: Boolean? = null

    var organismeId: UUID? = null
    var profilUtilisateurId: UUID? = null
}

class ImportUtilisateurData {
    var utilisateurList: MutableList<LigneImportUtilisateur> = arrayListOf()
}
