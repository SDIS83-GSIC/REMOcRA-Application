package remocra.web.tournee

import com.google.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.TourneeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.usecases.tournee.CreateTourneeUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/tournee")
@Produces(MediaType.APPLICATION_JSON)
class TourneeEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var tourneeRepository: TourneeRepository

    @Inject
    lateinit var createTourneeUseCase: CreateTourneeUseCase

    @Context
    lateinit var securityContext: SecurityContext

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

    @POST
    @Path("/createTournee")
    @RequireDroits([Droit.TOURNEE_A])
    fun createTournee(tourneeInput: TourneeInput): Response =
        createTourneeUseCase.execute(
            userInfo = securityContext.userInfo,
            element = Tournee(
                tourneeId = UUID.randomUUID(),
                tourneeActif = true,
                tourneeOrganismeId = tourneeInput.tourneeOrganismeId,
                tourneeLibelle = tourneeInput.tourneeLibelle,
                tourneeEtat = null,
                tourneeReservationUtilisateurId = null,
                tourneeDateSynchronisation = null,
            ),
        ).wrap()

    class TourneeInput {
        @FormParam("tourneeLibelle")
        lateinit var tourneeLibelle: String

        @FormParam("tourneeOrganismeId")
        lateinit var tourneeOrganismeId: UUID
    }
}
