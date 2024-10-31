package remocra.data

import java.util.UUID

data class LienProfilFonctionnaliteData(
    val profilOrganismeId: UUID,
    val profilUtilisateurId: UUID,
    val profilDroitId: UUID,
    val profilOrganismeLibelle: String,
    val profilUtilisateurLibelle: String,
    val profilDroitLibelle: String,
)
