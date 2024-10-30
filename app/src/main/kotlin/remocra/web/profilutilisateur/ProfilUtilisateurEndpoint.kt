package remocra.web.profilutilisateur

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.db.ProfilUtilisateurRepository
import remocra.web.AbstractEndpoint

@Path("/profil-utilisateur")
@Produces(MediaType.APPLICATION_JSON)
class ProfilUtilisateurEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var profilUtilisateurRepository: ProfilUtilisateurRepository

    @GET
    @Path("/")
    @Public("L'affichage des profils utilisateur n'est pas lié à un droit (par exemple : les filtres)")
    fun getProfilUtilisateur() =
        Response.ok(profilUtilisateurRepository.getAll()).build()
}
