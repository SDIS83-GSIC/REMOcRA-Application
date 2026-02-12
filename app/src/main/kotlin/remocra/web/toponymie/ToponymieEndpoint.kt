package remocra.web.toponymie

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.usecase.toponymie.ToponymieUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/toponymie")
@Produces(MediaType.APPLICATION_JSON)
class ToponymieEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var toponymieUseCase: ToponymieUseCase

    @GET
    @Path("/get-libelle-toponymie")
    @Public("Les toponymies ne sont pas liées à un droit.")
    @Produces(MediaType.APPLICATION_JSON)
    fun getToponymieForSelect(): Response {
        return Response.ok(
            toponymieUseCase.getToponymieForSelect(),
        ).build()
    }

    @GET
    @Path("/parametres/get-toponymies")
    @Public("Les toponymies ne sont pas liées à un droit.")
    fun getToponymies(
        @QueryParam("libelle") libelle: String,
        @QueryParam("dependenceObjId") communeId: UUID?,
    ): Response {
        return Response.ok(
            toponymieUseCase.getParametreToponymies(libelle, communeId),
        ).build()
    }
}
