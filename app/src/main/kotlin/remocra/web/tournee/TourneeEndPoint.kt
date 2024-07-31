package remocra.web.tournee

import com.google.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.TourneeRepository
import remocra.db.jooq.remocra.enums.Droit

@Path("/tournee")
@Produces(MediaType.APPLICATION_JSON)
class TourneeEndPoint {
    @Inject
    lateinit var tourneeRepository: TourneeRepository

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun getAllTourneeComplete(params: Params<TourneeRepository.Filter, TourneeRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                tourneeRepository.getAllTourneeComplete(params),
                tourneeRepository.countAllTournee(params),
            ),
        ).build()
}
