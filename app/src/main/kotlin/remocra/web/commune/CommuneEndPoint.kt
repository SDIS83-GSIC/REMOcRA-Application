package remocra.web.commune

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.usecases.commune.CommuneUseCase

@Path("/commune")
@Produces(MediaType.APPLICATION_JSON)
class CommuneEndPoint {

    @Inject
    lateinit var communeUseCase: CommuneUseCase

    @GET
    @Path("/get-libelle-commune")
    @Public("Les communes ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getCommuneForSelect(): Response {
        return Response.ok(
            communeUseCase.getCommuneForSelect(),
        )
            .build()
    }
}
