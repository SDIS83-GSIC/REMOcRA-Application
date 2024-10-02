package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.admin.ParametresData
import remocra.usecase.admin.ParametresUseCase

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
class AdminEndpoint {

    @Inject
    private lateinit var parametresUseCase: ParametresUseCase

    @GET
    @Path("/parametres")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getParametresData(): Response {
        return Response.ok(parametresUseCase.getParametresData()).build()
    }

    @PUT
    @Path("/parametres")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun updateParametres(parametres: ParametresData): Response {
        return Response.ok(parametresUseCase.updateParametres(parametres)).build()
    }
}
