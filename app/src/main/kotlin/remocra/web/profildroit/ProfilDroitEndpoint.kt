package remocra.web.profildroit

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.ProfilDroitRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/profil-droit")
@Produces(MediaType.APPLICATION_JSON)
class ProfilDroitEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var profilDroitRepository: ProfilDroitRepository

    @GET
    @Path("/")
    @RequireDroits([Droit.DOCUMENTS_R])
    fun getProfilDroit() =
        Response.ok(profilDroitRepository.getAll()).build()
}
