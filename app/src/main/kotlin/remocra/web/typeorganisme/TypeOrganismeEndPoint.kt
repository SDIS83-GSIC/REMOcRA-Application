package remocra.web.typeorganisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.TypeOrganismeRepository
import remocra.security.NoCsrf

@Path("/type-organisme")
@Produces(MediaType.APPLICATION_JSON)
class TypeOrganismeEndPoint {
    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les types organisme ne sont pas liés à un droit")
    fun getActive(): Response {
        return Response.ok(typeOrganismeRepository.getAll()).build()
    }
}
