package remocra.data

import java.util.UUID

data class LienProfilFonctionnaliteData(
    val profilOrganismeId: UUID,
    val profilUtilisateurId: UUID,
    val groupeFonctionnalitesId: UUID,
    val profilOrganismeLibelle: String,
    val profilUtilisateurLibelle: String,
    val groupeFonctionnalitesLibelle: String,
)
