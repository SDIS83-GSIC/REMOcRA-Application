package remocra.web.profildroit

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.ProfilDroitRepository
import remocra.web.AbstractEndpoint

@Path("/profil-droit")
@Produces(MediaType.APPLICATION_JSON)
class ProfilDroitEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var profilDroitRepository: ProfilDroitRepository

    @GET
    @Path("/")
    @Public("L'affichage des profils droits n'est pas lié à un droit (par exemple : les filtres)")
    fun getProfilDroit() =
        Response.ok(profilDroitRepository.getAll()).build()
}
