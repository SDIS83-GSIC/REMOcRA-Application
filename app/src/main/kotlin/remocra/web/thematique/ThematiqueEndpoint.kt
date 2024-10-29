package remocra.web.thematique

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.ThematiqueRepository
import remocra.web.AbstractEndpoint

@Path("/thematique")
@Produces(MediaType.APPLICATION_JSON)
class ThematiqueEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var thematiqueRepository: ThematiqueRepository

    @GET
    @Path("/")
    @Public("Les thématiques ne sont pas liées à un droit")
    fun getThematique() =
        Response.ok(thematiqueRepository.getAll()).build()
}
