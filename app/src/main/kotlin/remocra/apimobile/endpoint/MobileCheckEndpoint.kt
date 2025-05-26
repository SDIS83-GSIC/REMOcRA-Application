package remocra.apimobile.endpoint

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.apimobile.usecase.CheckUrlUseCase
import remocra.auth.Public
import remocra.web.AbstractEndpoint

@Path("/mobile/check")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class MobileCheckEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var checkUrlUseCase: CheckUrlUseCase

    @Path("/")
    @Public("Point d'entrée pour tester l'accessibilité du serveur")
    @PUT
    fun check(): Response {
        return Response.ok(checkUrlUseCase.execute()).build()
    }
}
