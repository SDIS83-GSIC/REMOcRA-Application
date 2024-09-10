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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.VisiteData
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.usecases.visites.CreateVisiteUseCase
import remocra.usecases.visites.DeleteVisiteUseCase
import remocra.usecases.visites.GetVisiteWithAnomalies
import remocra.web.AbstractEndpoint
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Path("/visite")
@Produces(MediaType.APPLICATION_JSON)
class VisiteEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var getVisiteWithAnomalies: GetVisiteWithAnomalies

    @Inject
    lateinit var createVisiteUseCase: CreateVisiteUseCase

    @Inject
    lateinit var deleteVisiteUseCase: DeleteVisiteUseCase

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/getVisiteWithAnomalies/{peiId}")
    @RequireDroits([Droit.VISITE_R])
    fun getVisiteWithAnomalies(
        @PathParam("peiId") peiId: UUID,
    ): Response {
        val dataToSend = DataToSendVisite(
            listVisite = getVisiteWithAnomalies.getVisiteWithAnomalies(peiUUID = peiId),
            typePei = peiRepository.getTypePei(idPei = peiId),
            lastCDP = visiteRepository.getLastVisiteDebitPression(peiId = peiId),
        )
        return Response.ok().entity(dataToSend).build()
    }

    data class DataToSendVisite(
        val listVisite: List<VisiteRepository.VisiteComplete>,
        val typePei: TypePei,
        val lastCDP: VisiteCtrlDebitPression?,
    )

    @PUT
    @Path("/createVisite")
    @RequireDroits([Droit.VISITE_CONTROLE_TECHNIQUE_C, Droit.VISITE_NON_PROGRAMME_C, Droit.VISITE_RECEP_C, Droit.VISITE_RECO_C, Droit.VISITE_RECO_INIT_C])
    fun createVisite(visiteInput: VisiteInput): Response {
        val generatedVisiteId = UUID.randomUUID()
        val visite = VisiteData(
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

    @DELETE
    @Path("/{visiteId}")
    @RequireDroits([Droit.VISITE_NP_D, Droit.VISITE_RECEP_D, Droit.VISITE_CTP_D, Droit.VISITE_RECO_D, Droit.VISITE_RECO_INIT_D])
    fun deleteVisite(@PathParam("visiteId") visiteId: UUID): Response {
        val visiteComplete = visiteRepository.getVisiteCompleteByVisiteId(visiteId)
        val ctrl = visiteRepository.getCtrlByVisiteId(visiteId)
        val listAnomalies = visiteRepository.getAnomaliesFromVisite(visiteId)

        val visiteDataToDelete = VisiteData(
            visiteId = visiteId,
            visitePeiId = visiteComplete.visitePeiId,
            visiteDate = visiteComplete.visiteDate,
            visiteTypeVisite = visiteComplete.visiteTypeVisite,
            visiteAgent1 = visiteComplete.visiteAgent1,
            visiteAgent2 = visiteComplete.visiteAgent2,
            visiteObservation = visiteComplete.visiteObservation,
            listeAnomalie = listAnomalies.toList(),
            isCtrlDebitPression = ctrl != null,
            ctrlDebitPression = CreationVisiteCtrl(
                ctrlDebit = ctrl?.visiteCtrlDebitPressionDebit,
                ctrlPression = ctrl?.visiteCtrlDebitPressionPression,
                ctrlPressionDyn = ctrl?.visiteCtrlDebitPressionPressionDyn,
            ),
        )
        return deleteVisiteUseCase.execute(
            userInfo = securityContext.userInfo,
            element = visiteDataToDelete,
        ).wrap()
    }
}
