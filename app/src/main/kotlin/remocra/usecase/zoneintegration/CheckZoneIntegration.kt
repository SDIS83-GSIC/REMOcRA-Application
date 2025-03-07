package remocra.usecase.zoneintegration

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.ZoneIntegrationRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText

class CheckZoneIntegration @Inject constructor(
    private val zoneIntegrationRepository: ZoneIntegrationRepository,
) : AbstractUseCase() {
    fun checkZoneIntegration(userInfo: UserInfo?, geometry: Geometry): Result {
        if (userInfo?.isSuperAdmin == true) {
            return Result.Success(true)
        }

        if (userInfo?.organismeId == null) {
            throw ForbiddenException()
        }

        val check = zoneIntegrationRepository.checkByOrganismeId(geometry.toGeomFromText(), userInfo.organismeId!!)

        return if (check == true) Result.Success(check) else throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_GEOMETRIE_FORBIDDEN)
    }
}
