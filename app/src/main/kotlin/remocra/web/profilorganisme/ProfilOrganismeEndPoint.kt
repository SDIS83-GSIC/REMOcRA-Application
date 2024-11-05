package remocra.web.profilorganisme

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.ProfilOrganismeRepository
import remocra.security.NoCsrf

@Path("/profil-organisme")
@Produces(MediaType.APPLICATION_JSON)
class ProfilOrganismeEndPoint {
    @Inject
    lateinit var profilOrganismeRepository: ProfilOrganismeRepository

    @GET
    @Path("/get-active")
    @NoCsrf("")
    @Public("Les profils organisme ne sont pas liés à un droit")
    fun getActive(): Response {
        return Response.ok(profilOrganismeRepository.getActive()).build()
    }
}
