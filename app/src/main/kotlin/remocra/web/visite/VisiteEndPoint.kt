package remocra.web.visite

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.usecases.visites.GetVisiteWithAnomalies
import java.util.UUID

@Path("/visite")
@Produces(MediaType.APPLICATION_JSON)
class VisiteEndPoint {
    @Inject
    lateinit var getVisiteWithAnomalies: GetVisiteWithAnomalies

    @GET
    @Path("/getVisiteWithAnomalies/{peiId}")
    fun getVisiteWithAnomalies(
        @PathParam("peiId") peiId: UUID,
    ): Response {
        return Response.ok().entity(getVisiteWithAnomalies.getVisiteWithAnomalies(peiUUID = peiId)).build()
    }
}
