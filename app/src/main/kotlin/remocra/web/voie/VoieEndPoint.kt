package remocra.web.voie

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.db.VoieRepository
import java.util.UUID

@Path("/voie")
@Produces(MediaType.APPLICATION_JSON)
class VoieEndPoint {

    @Inject
    lateinit var voieRepository: VoieRepository

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/{communeId}")
    @Public("Les voies ne sont pas liées à un droit")
    fun getVoieByZoneIntegrationShortData(@PathParam("communeId") communeId: UUID): Response =
        Response.ok().entity(voieRepository.getVoieByZoneIntegrationShortData(communeId, securityContext.userInfo!!)).build()

    @GET
    @Path("/get")
    @Public("Les voies ne sont pas liées à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getVoieForSelect(): Response {
        return Response.ok(
            voieRepository.getVoies(),
        )
            .build()
    }

    @GET
    @Path("/{voieId}/geometrie")
    @Public("Les voies ne sont pas liées à un droit")
    fun getGeometrieById(@PathParam("voieId") voieId: UUID): Response {
        return Response.ok(voieRepository.getGeometrieVoie(voieId)).build()
    }
}
