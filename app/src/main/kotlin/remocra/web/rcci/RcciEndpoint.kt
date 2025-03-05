package remocra.web.rcci

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
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
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.RcciForm
import remocra.data.RcciFormInput
import remocra.data.RcciGeometryForm
import remocra.data.enums.TypeElementCarte
import remocra.db.RcciRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.rcci.CreateRcciUseCase
import remocra.usecase.rcci.DeleteRcciUseCase
import remocra.usecase.rcci.SelectRcciUseCase
import remocra.usecase.rcci.UpdateRcciGeometryUseCase
import remocra.usecase.rcci.UpdateRcciUseCase
import remocra.utils.badRequest
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/rcci")
@Produces(MediaType.APPLICATION_JSON)
class RcciEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject lateinit var rcciRepository: RcciRepository

    @Inject lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject lateinit var selectRcciUseCase: SelectRcciUseCase

    @Inject lateinit var createRcciUseCase: CreateRcciUseCase

    @Inject lateinit var updateRcciUseCase: UpdateRcciUseCase

    @Inject lateinit var updateRcciGeometryUseCase: UpdateRcciGeometryUseCase

    @Inject lateinit var deleteRcciUseCase: DeleteRcciUseCase

    @GET
    @Path("/layer")
    @RequireDroits([Droit.RCCI_A, Droit.RCCI_R])
    @Produces(MediaType.APPLICATION_JSON)
    fun get(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response =
        Response.ok(getPointCarteUseCase.execute(bbox, srid, null, TypeElementCarte.RCCI, securityContext.userInfo!!)).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.RCCI_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun post(@Context httpRequest: HttpServletRequest): Response =
        createRcciUseCase.execute(
            securityContext.userInfo,
            RcciFormInput(
                rcci = objectMapper.readValue<RcciForm>(httpRequest.getTextPart("rcci")),
                documentList = httpRequest.parts.filter { it.name.startsWith("document") },
            ),
        ).wrap()

    @GET
    @Path("/{rcciId}")
    @RequireDroits([Droit.RCCI_A])
    fun get(@PathParam("rcciId") rcciId: UUID): Response =
        selectRcciUseCase.execute(securityContext.userInfo, rcciId).wrap()

    @PUT
    @Path("/{rcciId}")
    @RequireDroits([Droit.RCCI_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun put(@Context httpRequest: HttpServletRequest, @PathParam("rcciId") rcciId: UUID): Response {
        val rcci = objectMapper.readValue<RcciForm>(httpRequest.getTextPart("rcci"))
        if (rcci.rcciId != rcciId) {
            return badRequest().build()
        }
        return updateRcciUseCase.execute(
            securityContext.userInfo,
            RcciFormInput(
                rcci = rcci,
                documentList = httpRequest.parts.filter { it.name.startsWith("document") },
            ),
        ).wrap()
    }

    @PATCH
    @Path("/{rcciId}/geometry")
    @RequireDroits([Droit.RCCI_A])
    fun move(element: RcciGeometryForm, @PathParam("rcciId") rcciId: UUID): Response {
        if (element.rcciId != rcciId) {
            return badRequest().build()
        }
        return updateRcciGeometryUseCase.execute(securityContext.userInfo, element).wrap()
    }

    @DELETE
    @Path("/{rcciId}")
    @RequireDroits([Droit.RCCI_A])
    fun delete(@PathParam("rcciId") rcciId: UUID): Response =
        deleteRcciUseCase.execute(securityContext.userInfo, rcciRepository.selectRcci(rcciId)).wrap()

    @GET
    @Path("/refs")
    @RequireDroits([Droit.RCCI_A])
    fun refs(): Response =
        Response.ok(
            object {
                val ddtmonf = utilisateurRepository.getUtilisateurDdtmonf()
                val sdis = utilisateurRepository.getUtilisateurSdis()
                val gendarmerie = utilisateurRepository.getUtilisateurGendarmerie()
                val police = utilisateurRepository.getUtilisateurPolice()
            },
        ).build()
}
