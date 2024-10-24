package remocra.data

import java.util.UUID

data class SiteData(
    val siteId: UUID,
    val siteCode: String,
    val siteLibelle: String,
    val siteGestionnaireId: UUID?,
    val siteActif: Boolean,
)
