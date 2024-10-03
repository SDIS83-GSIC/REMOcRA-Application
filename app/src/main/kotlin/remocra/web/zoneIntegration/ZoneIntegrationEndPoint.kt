package remocra.web.zoneIntegration

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.data.CoordonneeInput
import remocra.db.ZoneIntegrationRepository
import remocra.security.NoCsrf
import remocra.usecase.zoneintegration.CheckZoneIntegration
import remocra.web.AbstractEndpoint

@Path("/zone-integration")
@Produces(MediaType.APPLICATION_JSON)
class ZoneIntegrationEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    @Inject
    lateinit var checkZoneIntegration: CheckZoneIntegration

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les zones d'intégration ne sont pas liées à un droit")
    fun getActive(): Response {
        return Response.ok(zoneIntegrationRepository.getAll()).build()
    }

    /**
     * Vérifie si la géométrie est contenue dans la zone de l'utilisateur
     */
    @POST
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    @Public("La vérification doit être accessible pour n'importe quel utilisateur")
    fun check(input: CoordonneeInput): Response =
        checkZoneIntegration.checkZoneIntegration(securityContext.userInfo, input).wrap()
}
