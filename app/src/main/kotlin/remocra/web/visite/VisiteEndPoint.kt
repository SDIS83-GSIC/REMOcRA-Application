package remocra.web.visite

import com.google.inject.Inject
import jakarta.ws.rs.DELETE
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
import remocra.authn.userInfo
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.usecases.visites.CreateVisiteUseCase
import remocra.usecases.visites.DeleteVisiteUseCase
import remocra.usecases.visites.GetVisiteWithAnomalies
import remocra.web.wrap
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Path("/visite")
@Produces(MediaType.APPLICATION_JSON)
class VisiteEndPoint {
    @Inject
    lateinit var getVisiteWithAnomalies: GetVisiteWithAnomalies

    @Inject
    lateinit var createVisiteUseCase: CreateVisiteUseCase

    @Inject
    lateinit var deleteVisiteUseCase: DeleteVisiteUseCase

    @Inject
    lateinit var peiRepository: PeiRepository

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/getVisiteWithAnomalies/{peiId}")
    fun getVisiteWithAnomalies(
        @PathParam("peiId") peiId: UUID,
    ): Response {
        val dataToSend = DataToSendVisite(
            listVisite = getVisiteWithAnomalies.getVisiteWithAnomalies(peiUUID = peiId),
            typePei = peiRepository.getTypePei(idPei = peiId),
        )
        return Response.ok().entity(dataToSend).build()
    }

    data class DataToSendVisite(
        val listVisite: List<VisiteRepository.VisiteComplete>,
        val typePei: TypePei,
    )

    @PUT
    @Path("/createVisite")
    fun createVisite(visiteInput: VisiteInput): Response {
        val generatedVisiteId = UUID.randomUUID()
        val visite = VisiteCompleteToInsert(
            visiteId = generatedVisiteId,
            visitePeiId = visiteInput.visitePeiId,
            visiteDate = visiteInput.visiteDate,
            visiteTypeVisite = visiteInput.visiteTypeVisite,
            visiteAgent1 = visiteInput.visiteAgent1,
            visiteAgent2 = visiteInput.visiteAgent2,
            visiteObservation = visiteInput.visiteObservation,
            listeAnomalie = visiteInput.listeAnomalie,
            isCtrlDebitPression = visiteInput.isCtrlDebitPression,
            ctrlDebitPression = visiteInput.ctrlDebitPression,
        )
        return createVisiteUseCase.execute(userInfo = securityContext.userInfo, element = visite).wrap()
    }

    class VisiteInput {
        @FormParam("visitePeiId")
        lateinit var visitePeiId: UUID

        @FormParam("visiteDate")
        lateinit var visiteDate: ZonedDateTime

        @FormParam("visiteTypeVisite")
        lateinit var visiteTypeVisite: TypeVisite

        @FormParam("visiteAgent1")
        val visiteAgent1: String? = null

        @FormParam("visiteAgent2")
        val visiteAgent2: String? = null

        @FormParam("visiteObservation")
        val visiteObservation: String? = null

        @FormParam("listeAnomalie")
        val listeAnomalie: List<UUID> = listOf()

        @FormParam("isCtrlDebitPression")
        val isCtrlDebitPression: Boolean = false

        @FormParam("ctrlDebitPression")
        val ctrlDebitPression: CreationVisiteCtrl? = null
    }

    /** Reprend les attributs du Pojo VisiteCtrlDebitPression
     * en faisant abstraction de visiteId, non défini lors de la création d'un visite
     */
    data class CreationVisiteCtrl(
        val ctrlDebit: Int?,
        val ctrlPression: BigDecimal?,
        val ctrlPressionDyn: BigDecimal?,
    )

    data class VisiteCompleteToInsert(
        val visiteId: UUID,
        val visitePeiId: UUID,
        val visiteDate: ZonedDateTime,
        val visiteTypeVisite: TypeVisite,
        val visiteAgent1: String?,
        val visiteAgent2: String?,
        val visiteObservation: String?,
        var listeAnomalie: List<UUID>,
        val isCtrlDebitPression: Boolean,
        var ctrlDebitPression: CreationVisiteCtrl?,
    )

    @DELETE
    @Path("/{visiteId}")
    fun deleteVisite(
        @PathParam("visiteId") visiteId: UUID,
    ): Response {
        return deleteVisiteUseCase.execute(userInfo = securityContext.userInfo, element = visiteId).wrap()
    }
}
