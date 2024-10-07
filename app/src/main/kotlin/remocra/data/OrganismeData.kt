package remocra.data

import java.util.UUID

data class OrganismeData(
    val organismeId: UUID,
    val organismeActif: Boolean,
    val organismeCode: String,
    val organismeLibelle: String,
    val organismeEmailContact: String?,
    val organismeProfilOrganismeId: UUID,
    val organismeTypeOrganismeId: UUID,
    val organismeZoneIntegrationId: UUID,
    val organismeParentId: UUID?,
)
