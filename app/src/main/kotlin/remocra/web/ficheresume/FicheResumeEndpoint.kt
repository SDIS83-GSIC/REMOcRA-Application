package remocra.web.ficheresume

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.RequireDroits
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.ficheresume.BuildFicheResumeUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/fiche-resume")
@Produces(MediaType.APPLICATION_JSON)
class FicheResumeEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var buildFicheResumeUseCase: BuildFicheResumeUseCase

    @GET
    @Path("/{peiId}")
    @RequireDroits([Droit.PEI_R])
    fun getElementFiche(
        @PathParam("peiId")
        peiId: UUID,
    ) =
        Response.ok(buildFicheResumeUseCase.execute(peiId)).build()
}
