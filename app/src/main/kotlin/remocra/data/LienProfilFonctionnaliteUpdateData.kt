package remocra.data

import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeGroupeFonctionnalites
import java.util.UUID

data class LienProfilFonctionnaliteUpdateData(
    val profilOrganismeId: UUID,
    val profilUtilisateurId: UUID,
    val newValue: LProfilUtilisateurOrganismeGroupeFonctionnalites,
)
