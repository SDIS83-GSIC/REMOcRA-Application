package remocra.usecase.zoneintegration

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.data.enums.ErrorType
import remocra.db.ZoneIntegrationRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText
import java.util.UUID

class ComputeZoneSpecialeUseCase @Inject constructor(
    private val zoneIntegrationRepository: ZoneIntegrationRepository,
) : AbstractUseCase() {
    fun computeZoneSpeciale(geometry: Geometry): UUID? {
        val zoneSpecialeIds = zoneIntegrationRepository.getZSIdsByGeometrie(geometry.toGeomFromText())
        if (zoneSpecialeIds.size > 1) {
            throw RemocraResponseException(ErrorType.ZONE_SPECIALE_MULTIPLE)
        } else {
            return zoneSpecialeIds.firstOrNull()
        }
    }
}
