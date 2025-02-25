package remocra.usecase.zoneintegration

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKTReader
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.CoordonneeInput
import remocra.data.enums.ErrorType
import remocra.db.ZoneIntegrationRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.sridFromEpsgCode
import remocra.utils.toGeomFromText

class CheckZoneIntegration @Inject constructor(
    private val zoneIntegrationRepository: ZoneIntegrationRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {
    fun checkZoneIntegration(userInfo: UserInfo?, input: CoordonneeInput): Result {
        if (userInfo?.organismeId == null) {
            throw ForbiddenException()
        }

        val geometry: Geometry = try {
            WKTReader().read(input.wkt)
        } catch (e: ParseException) {
            throw RemocraResponseException(ErrorType.BAD_GEOMETRIE)
        }
        geometry.srid = sridFromEpsgCode(input.srid)

        val check = zoneIntegrationRepository.checkByOrganismeId(geometry.toGeomFromText(), userInfo.organismeId!!, appSettings.srid)

        return if (check == true) Result.Success(check) else throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_GEOMETRIE_FORBIDDEN)
    }
}
