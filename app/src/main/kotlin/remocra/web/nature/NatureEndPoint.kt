package remocra.web.nature

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.usecase.nature.NatureUseCase

@Path("/nature")
@Produces(MediaType.APPLICATION_JSON)
class NatureEndPoint {

    @Inject
    lateinit var natureUseCase: NatureUseCase

    @GET
    @Path("/get-libelle-nature")
    @Public("Les natures ne sont pas liés à un droit")
    @Produces(MediaType.APPLICATION_JSON)
    fun getNatureForSelect(): Response {
        return Response.ok(
            natureUseCase.getNatureForSelect(),
        )
            .build()
    }
}
