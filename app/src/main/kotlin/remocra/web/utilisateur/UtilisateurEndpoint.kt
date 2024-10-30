package remocra.web.utilisateur

import com.google.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint

@Path("/utilisateur")
@Produces(MediaType.APPLICATION_JSON)
class UtilisateurEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var utilisateurRepository: UtilisateurRepository

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_UTILISATEURS_R])
    fun getAll(params: Params<UtilisateurRepository.Filter, UtilisateurRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                list = utilisateurRepository.getAllForAdmin(params),
                count = utilisateurRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
}
