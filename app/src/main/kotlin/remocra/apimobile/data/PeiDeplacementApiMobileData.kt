package remocra.apimobile.data

import java.util.UUID

data class PeiDeplacementApiMobileData(
    val peiId: UUID,
    val tourneeId: UUID,
    val lon: Double,
    val lat: Double,
)
