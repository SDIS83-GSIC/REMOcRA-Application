package remocra.web.pei

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.PeiAvecTournees
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.pei.DesaffecterTourneesUseCase
import remocra.usecase.pei.GetPeiTableTourneeByPeiUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/pei-desaffecter-tournee")
@Produces(MediaType.APPLICATION_JSON)
class PeiDesaffecterTourneeEndPoint : AbstractEndpoint() {

    @Inject lateinit var desaffecterTourneesUseCase: DesaffecterTourneesUseCase

    @Inject lateinit var getPeiTableTourneeByPeiUseCase: GetPeiTableTourneeByPeiUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Path("/get-pei-tournee")
    @GET
    @RequireDroits([Droit.TOURNEE_R])
    fun getPeiTableTournee(
        @QueryParam("peiIds") peiIds: Set<UUID>,
    ): Response {
        return Response.ok(
            getPeiTableTourneeByPeiUseCase.execute(
                peiIds = peiIds,
                userInfo = securityContext.userInfo,
            ),
        ).build()
    }

    @PUT
    @Path("/desaffecter-tournee")
    @RequireDroits([Droit.TOURNEE_DESAFFECTER_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun desaffecterTournees(
        listPeiTableTournee: List<PeiAvecTournees>,
    ): Response {
        return desaffecterTourneesUseCase.execute(securityContext.userInfo, listPeiTableTournee).wrap()
    }
}
