package remocra.web.pei

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.ficheresume.HistoriqueDebitChart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/pibi")
@Produces(MediaType.APPLICATION_JSON)
class PibiEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var historiqueDebitChart: HistoriqueDebitChart

    @GET
    @Path("/historique/{pibiId}")
    @RequireDroits([Droit.PEI_R])
    fun getDataChartFicheResume(
        @PathParam("pibiId")
        pibiId: UUID,
    ): Response {
        return Response.ok(historiqueDebitChart.execute(pibiId)).build()
    }
}
