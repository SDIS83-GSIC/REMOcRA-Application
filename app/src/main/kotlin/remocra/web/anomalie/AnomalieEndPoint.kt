package remocra.web.anomalie

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.AnomalieRepository
import remocra.db.jooq.remocra.enums.Droit
import java.util.UUID

@Path("/anomalie")
@Produces(MediaType.APPLICATION_JSON)
class AnomalieEndPoint {

    @Inject lateinit var anomalieRepository: AnomalieRepository

    @GET
    @Path("/getAssignablesAnomalies/{peiId}")
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A, Droit.VISITE_CONTROLE_TECHNIQUE_C, Droit.VISITE_NON_PROGRAMME_C, Droit.VISITE_RECEP_C, Droit.VISITE_RECO_C, Droit.VISITE_RECO_INIT_C])
    fun getAssignableAnomalie(
        @PathParam("peiId") peiId: UUID,
    ): Response {
        return Response.ok()
            .entity(anomalieRepository.getAllAnomalieAssignable(peiId))
            .build()
    }

    @GET
    @Path("/getAssignablesAnomaliesByTourneeId/{tourneeId}")
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun getAssignableAnomalieByTourneeId(
        @PathParam("tourneeId") tourneeId: UUID,
    ): Response {
        return Response.ok()
            .entity(anomalieRepository.getAllAnomalieAssignableByPeiTourneeId(tourneeId))
            .build()
    }
}
