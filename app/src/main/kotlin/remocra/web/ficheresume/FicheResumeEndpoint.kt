package remocra.web.ficheresume

import com.google.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
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
import remocra.data.FicheResumeBlocData
import remocra.db.FicheResumeRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.ficheresume.BuildFicheResumeUseCase
import remocra.usecase.ficheresume.FicheResumeUpsertUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/fiche-resume")
@Produces(MediaType.APPLICATION_JSON)
class FicheResumeEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var buildFicheResumeUseCase: BuildFicheResumeUseCase

    @Inject
    lateinit var ficheResumeRepository: FicheResumeRepository

    @Inject
    lateinit var ficheResumeUpsertUseCase: FicheResumeUpsertUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/{peiId}")
    @RequireDroits([Droit.PEI_R])
    fun getElementFiche(
        @PathParam("peiId")
        peiId: UUID,
    ) =
        Response.ok(buildFicheResumeUseCase.execute(peiId)).build()

    @GET
    @Path("/get-blocs")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun getForAdmin(): Response {
        return Response.ok(ficheResumeRepository.getFicheResume()).build()
    }

    @PUT
    @Path("/upsert")
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    @Produces(MediaType.APPLICATION_JSON)
    fun upsert(
        ficheResumeInput: FicheResumeInput,
    ) =
        ficheResumeUpsertUseCase.execute(
            securityContext.userInfo,
            ficheResumeInput.listeFicheResumeElement,
        ).wrap()

    class FicheResumeInput {
        @FormParam("listeFicheResumeElement")
        lateinit var listeFicheResumeElement: Collection<FicheResumeBlocData>
    }
}
