package remocra.web.voie

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.VoieRepository

@Path("/voie")
@Produces(MediaType.APPLICATION_JSON)
class VoieEndPoint {

    @Inject
    lateinit var voieRepository: VoieRepository

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
}
