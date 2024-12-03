package remocra.apimobile.endpoint

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.apimobile.usecase.BuildReferentielUseCase
import remocra.auth.AuthDevice
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.web.AbstractEndpoint

@Path("/mobile/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class MobileReferentielEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var buildReferentielUseCase: BuildReferentielUseCase

    @Public("La récupération du référentiel n'est pas soumise à un droit")
    @AuthDevice
    @Path("/")
    @GET
    fun getRefentiel(): Response {
        return Response.ok(buildReferentielUseCase.execute(securityContext.userInfo!!)).build()
    }
}
