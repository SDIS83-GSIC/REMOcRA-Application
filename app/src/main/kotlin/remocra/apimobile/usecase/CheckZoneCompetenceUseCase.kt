package remocra.apimobile.usecase

import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.zoneintegration.CheckZoneCompetenceContainsUseCase

class CheckZoneCompetenceUseCase
@Inject constructor(
    private val checkZoneCompetenceContainsUseCase: CheckZoneCompetenceContainsUseCase,
) :
    AbstractUseCase() {

    fun execute(lon: Double, lat: Double, userInfo: WrappedUserInfo): Result {
        // on crée un objet Geometry avec les coordonnées passées
        try {
            val wkt = "POINT($lon $lat)"
            val fromText = WKTReader()
            val geometriePei: Geometry = fromText.read(wkt)

            geometriePei.srid = GlobalConstants.SRID_4326

            // Si la géométrie n'est pas dans une zone de compétence, on lève une exception
            checkZoneCompetenceContainsUseCase.checkContains(
                userInfo = userInfo,
                geometries = listOf(geometriePei),
            )

            return Result.Success()
        } catch (rre: RemocraResponseException) {
            // Par propreté, on transforme en "vrai" Forbidden
            if (rre.status == Response.Status.FORBIDDEN) {
                return Result.Forbidden(rre.message)
            }
            return Result.Error(rre.message)
        } catch (e: Exception) {
            return Result.Error(e.message)
        }
    }
}
