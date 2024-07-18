package remocra.web.anomalie

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.db.AnomalieRepository
import java.util.UUID

@Path("/anomalie")
@Produces(MediaType.APPLICATION_JSON)
class AnomalieEndPoint {

    @Inject lateinit var anomalieRepository: AnomalieRepository

    @GET
    @Path("/getAssignablesAnomalies/{peiId}")
    fun getAssignableAnomalie(
        @PathParam("peiId") peiId: UUID,
    ): Response {
        return Response.ok()
            .entity(anomalieRepository.getAllAnomalieAssignable(peiId))
            .build()
    }
}
