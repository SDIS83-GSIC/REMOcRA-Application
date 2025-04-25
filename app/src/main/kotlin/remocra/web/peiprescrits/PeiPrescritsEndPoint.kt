package remocra.web.peiprescrits

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.locationtech.jts.geom.Geometry
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.db.PeiPrescritRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.PeiPrescrit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.peiPrescrit.CreatePeiPrescritUseCase
import remocra.usecase.peiPrescrit.DeletePeiPrescritUseCase
import remocra.usecase.peiPrescrit.UpdatePeiPrescritUseCase
import remocra.web.AbstractEndpoint
import java.time.ZonedDateTime
import java.util.UUID

@Path("/pei-prescrit")
@Produces(MediaType.APPLICATION_JSON)
class PeiPrescritsEndPoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var peiPrescritRepository: PeiPrescritRepository

    @Inject lateinit var createPeiPrescritUseCase: CreatePeiPrescritUseCase

    @Inject lateinit var updatePeiPrescritUseCase: UpdatePeiPrescritUseCase

    @Inject lateinit var deletePeiPrescritUseCase: DeletePeiPrescritUseCase

    @GET
    @Path("/{peiPrescritId}")
    @RequireDroits([Droit.PEI_PRESCRIT_A])
    fun get(@PathParam("peiPrescritId") peiPrescritId: UUID) =
        Response.ok(peiPrescritRepository.getById(peiPrescritId)).build()

    @GET
    @Path("/layer")
    @RequireDroits([Droit.PEI_PRESCRIT_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox = bbox,
                sridSource = srid,
                typeElementCarte = TypeElementCarte.PEI_PRESCRIT,
                userInfo = securityContext.userInfo,
            ),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.PEI_PRESCRIT_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        peiPrescritInput: PeiPrescritInput,
    ): Response =
        createPeiPrescritUseCase.execute(
            securityContext.userInfo,
            PeiPrescrit(
                peiPrescritId = UUID.randomUUID(),
                peiPrescritDate = peiPrescritInput.peiPrescritDate,
                peiPrescritDebit = peiPrescritInput.peiPrescritDebit,
                peiPrescritNbPoteaux = peiPrescritInput.peiPrescritNbPoteaux,
                peiPrescritOrganismeId = securityContext.userInfo?.organismeId,
                peiPrescritCommentaire = peiPrescritInput.peiPrescritCommentaire,
                peiPrescritAgent = peiPrescritInput.peiPrescritAgent,
                peiPrescritNumDossier = peiPrescritInput.peiPrescritNumDossier,
                peiPrescritGeometrie = peiPrescritInput.peiPrescritGeometrie,
            ),
        ).wrap()

    @PUT
    @Path("/update/{peiPrescritId}")
    @RequireDroits([Droit.PEI_PRESCRIT_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("peiPrescritId") peiPrescritId: UUID,
        peiPrescritInput: PeiPrescritInput,
    ): Response =
        updatePeiPrescritUseCase.execute(
            securityContext.userInfo,
            PeiPrescrit(
                peiPrescritId = peiPrescritId,
                peiPrescritDate = peiPrescritInput.peiPrescritDate,
                peiPrescritDebit = peiPrescritInput.peiPrescritDebit,
                peiPrescritNbPoteaux = peiPrescritInput.peiPrescritNbPoteaux,
                peiPrescritCommentaire = peiPrescritInput.peiPrescritCommentaire,
                peiPrescritAgent = peiPrescritInput.peiPrescritAgent,
                peiPrescritNumDossier = peiPrescritInput.peiPrescritNumDossier,
                peiPrescritOrganismeId = securityContext.userInfo?.organismeId,
                peiPrescritGeometrie = peiPrescritInput.peiPrescritGeometrie,
            ),
        ).wrap()

    data class PeiPrescritInput(
        val peiPrescritDate: ZonedDateTime? = null,
        val peiPrescritDebit: Int? = null,
        val peiPrescritNbPoteaux: Int? = null,
        val peiPrescritCommentaire: String? = null,
        val peiPrescritAgent: String? = null,
        val peiPrescritNumDossier: String? = null,
        val peiPrescritGeometrie: Geometry,
    )

    @DELETE
    @Path("/{peiPrescritId}")
    @RequireDroits([Droit.PEI_PRESCRIT_A])
    fun delete(
        @PathParam("peiPrescritId") peiPrescritId: UUID,
    ): Response {
        val peiPrescrit = peiPrescritRepository.getById(peiPrescritId)
        return deletePeiPrescritUseCase.execute(securityContext.userInfo, peiPrescrit).wrap()
    }
}
