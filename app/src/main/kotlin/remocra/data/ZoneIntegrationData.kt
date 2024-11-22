package remocra.data

import java.util.UUID

data class ZoneIntegrationData(
    val zoneIntegrationId: UUID,
    val zoneIntegrationCode: String,
    val zoneIntegrationLibelle: String,
    val zoneIntegrationActif: Boolean,
)
