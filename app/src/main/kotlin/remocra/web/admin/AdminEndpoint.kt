package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
class AdminEndpoint {

    @Inject
    private lateinit var parametresUseCase: ParametresUseCase

    @GET
    @Path("/parametres")
    fun getParametresData(): Response {
        return Response.ok(parametresUseCase.getParametresData()).build()
    }

    @PUT
    @Path("/parametres")
    fun updateParametres(parametres: ParametresData): Response {
        return Response.ok(parametresUseCase.updateParametres(parametres)).build()
    }
}
