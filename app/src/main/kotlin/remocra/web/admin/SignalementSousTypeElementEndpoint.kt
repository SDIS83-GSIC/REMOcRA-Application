package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
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
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.SignalementRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.SignalementSousTypeElement
import remocra.usecase.signalementSousTypeElement.CreateSignalementSousTypeElementUseCase
import remocra.usecase.signalementSousTypeElement.DeleteSignalementSousTypeElementUseCase
import remocra.usecase.signalementSousTypeElement.UpdateSignalementSousTypeElementUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/signalement-sous-type-element")
@Produces(MediaType.APPLICATION_JSON)
class SignalementSousTypeElementEndpoint : AbstractEndpoint() {

    @Inject lateinit var signalementRepository: SignalementRepository

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var createSignalementSousTypeElementUseCase: CreateSignalementSousTypeElementUseCase

    @Inject lateinit var updateSignalementSousTypeElementUseCase: UpdateSignalementSousTypeElementUseCase

    @Inject lateinit var deleteSignalementSousTypeElementUseCase: DeleteSignalementSousTypeElementUseCase

    @POST
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getAllSousTypeElement(params: Params<SignalementRepository.Filter, SignalementRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                signalementRepository.getAllForAdmin(params),
                signalementRepository.countAllForAdmin(params),
            ),
        ).build()

    @GET
    @Path("/ref")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getRef() = Response.ok(signalementRepository.getType()).build()

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun create(signalementSousTypeElementInput: SignalementSousTypeElementInput): Response =
        createSignalementSousTypeElementUseCase.execute(
            userInfo = securityContext.userInfo,
            SignalementSousTypeElement(
                signalementSousTypeElementId = UUID.randomUUID(),
                signalementSousTypeElementCode = signalementSousTypeElementInput.signalementSousTypeElementCode,
                signalementSousTypeElementLibelle = signalementSousTypeElementInput.signalementSousTypeElementLibelle,
                signalementSousTypeElementActif = signalementSousTypeElementInput.signalementSousTypeElementActif,
                signalementSousTypeElementTypeElement = signalementSousTypeElementInput.signalementSousTypeElementTypeElement,
                signalementSousTypeElementTypeGeometrie = signalementSousTypeElementInput.signalementSousTypeElementTypeGeometrie,
            ),
        ).wrap()

    class SignalementSousTypeElementInput {
        @FormParam("signalementSousTypeElementCode")
        lateinit var signalementSousTypeElementCode: String

        @FormParam("signalementSousTypeElementLibelle")
        lateinit var signalementSousTypeElementLibelle: String

        @FormParam("signalementSousTypeElementActif")
        val signalementSousTypeElementActif: Boolean = true

        @FormParam("signalementSousTypeElementTypeElement")
        val signalementSousTypeElementTypeElement: UUID? = null

        @FormParam("signalementSousTypeElementTypeGeometrie")
        lateinit var signalementSousTypeElementTypeGeometrie: TypeGeometry
    }

    @PUT
    @Path("/update/{signalementSousTypeElementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun update(@PathParam("signalementSousTypeElementId") signalementSousTypeElementId: UUID, signalementSousTypeElementInput: SignalementSousTypeElementInput): Response =
        updateSignalementSousTypeElementUseCase.execute(
            securityContext.userInfo,
            SignalementSousTypeElement(
                signalementSousTypeElementId = signalementSousTypeElementId,
                signalementSousTypeElementCode = signalementSousTypeElementInput.signalementSousTypeElementCode,
                signalementSousTypeElementLibelle = signalementSousTypeElementInput.signalementSousTypeElementLibelle,
                signalementSousTypeElementActif = signalementSousTypeElementInput.signalementSousTypeElementActif,
                signalementSousTypeElementTypeElement = signalementSousTypeElementInput.signalementSousTypeElementTypeElement,
                signalementSousTypeElementTypeGeometrie = signalementSousTypeElementInput.signalementSousTypeElementTypeGeometrie,
            ),
        ).wrap()

    @GET
    @Path("/get/{signalementSousTypeElementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getSousTypeById(@PathParam("signalementSousTypeElementId") signalementSousTypeElementId: UUID) =
        Response.ok(signalementRepository.getById(signalementSousTypeElementId)).build()

    @DELETE
    @Path("/delete/{signalementSousTypeElementId}")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun delete(@PathParam("signalementSousTypeElementId") signalementSousTypeElementId: UUID) =
        deleteSignalementSousTypeElementUseCase.execute(
            securityContext.userInfo,
            signalementRepository.getById(signalementSousTypeElementId),
        ).wrap()
}
