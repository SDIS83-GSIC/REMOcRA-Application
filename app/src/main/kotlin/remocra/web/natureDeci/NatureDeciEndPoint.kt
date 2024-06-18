package remocra.web.natureDeci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.usecases.natureDeci.NatureDeciUseCase

@Path("/natureDeci")
@Produces(MediaType.APPLICATION_JSON)
class NatureDeciEndPoint {

    @Inject
    lateinit var natureDeciUseCase: NatureDeciUseCase

    @GET
    @Path("/get-libelle-natureDeci")
    @Produces(MediaType.APPLICATION_JSON)
    fun getNatureDeciForSelect(): Response {
        return Response.ok(
            natureDeciUseCase.getNatureDeciForSelect(),
        )
            .build()
    }
}
