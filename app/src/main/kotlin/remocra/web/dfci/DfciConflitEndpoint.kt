package remocra.web.dfci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.DfciConflitRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.dfci.ResolveDfciConflitUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/dfci-conflit")
@Produces(MediaType.APPLICATION_JSON)
class DfciConflitEndpoint : AbstractEndpoint() {

    @Inject
    private lateinit var dfciConflitRepository: DfciConflitRepository

    @Inject
    private lateinit var resolveDfciConflitUseCase: ResolveDfciConflitUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    @POST
    @Path("/list")
    @RequireDroits([Droit.DFCI_GESTION_CONFLITS_R])
    fun getAllDfciConflit(params: Params<DfciConflitRepository.FilterDfciConflit, DfciConflitRepository.SortDfciConflit>): Response =
        Response.ok(
            DataTableau(
                dfciConflitRepository.getAllDfciConflit(params),
                dfciConflitRepository.getCountAllDfciConflit(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/{dfciConflitId}")
    @RequireDroits([Droit.DFCI_GESTION_CONFLITS_A])
    fun getDfciConflit(
        @PathParam("dfciConflitId") dfciConflitId: UUID,
    ): Response =
        Response.ok(
            dfciConflitRepository.getDfciConflitById(dfciConflitId),
        ).build()

    @PUT
    @Path("/resolve")
    @RequireDroits([Droit.DFCI_GESTION_CONFLITS_A])
    fun resolveDfciConflit(dfciConflit: DfciConflit): Response =
        Response.ok(
            resolveDfciConflitUseCase.execute(securityContext.userInfo, dfciConflit),
        ).build()
}
