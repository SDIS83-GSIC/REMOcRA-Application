package remocra.web.zoneIntegration

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.ZoneIntegrationRepository
import remocra.security.NoCsrf

@Path("/zone-integration")
@Produces(MediaType.APPLICATION_JSON)
class ZoneIntegrationEndPoint {
    @Inject
    lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les zones d'intégration ne sont pas liées à un droit")
    fun getActive(): Response {
        return Response.ok(zoneIntegrationRepository.getAll()).build()
    }
}
