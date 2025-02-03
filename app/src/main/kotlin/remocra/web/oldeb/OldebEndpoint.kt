package remocra.web.oldeb

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
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
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.oldeb.OldebForm
import remocra.data.oldeb.OldebFormInput
import remocra.data.oldeb.OldebLocataireForm
import remocra.data.oldeb.OldebProprieteForm
import remocra.data.oldeb.OldebVisiteForm
import remocra.db.OldebRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.oldeb.CreateOldebUseCase
import remocra.usecase.oldeb.DeleteOldebUseCase
import remocra.usecase.oldeb.SelectOldebUseCase
import remocra.usecase.oldeb.UpdateOldebUseCase
import remocra.utils.badRequest
import remocra.utils.getTextPart
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/oldeb")
@Produces(MediaType.APPLICATION_JSON)
class OldebEndpoint : AbstractEndpoint() {

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var objectMapper: ObjectMapper

    @Inject lateinit var oldebRepository: OldebRepository

    @Inject lateinit var selectOldebUseCase: SelectOldebUseCase

    @Inject lateinit var createOldebUseCase: CreateOldebUseCase

    @Inject lateinit var updateOldebUseCase: UpdateOldebUseCase

    @Inject lateinit var deleteOldebUseCase: DeleteOldebUseCase

    @POST
    @Path("/")
    @RequireDroits([Droit.OLDEB_R])
    fun list(params: Params<OldebRepository.Filter, OldebRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                oldebRepository.getList(params),
                oldebRepository.getCount(params.filterBy),
            ),
        ).build()

    @GET
    @Path("/{oldebId}")
    @RequireDroits([Droit.OLDEB_R])
    fun get(@PathParam("oldebId") oldebId: UUID): Response =
        selectOldebUseCase.execute(securityContext.userInfo, oldebId).wrap()

    @POST
    @Path("/create")
    @RequireDroits([Droit.OLDEB_C])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun create(
        @Context httpRequest: HttpServletRequest,
    ): Response =
        createOldebUseCase.execute(
            userInfo = securityContext.userInfo,
            element = OldebFormInput(
                oldeb = objectMapper.readValue<OldebForm>(httpRequest.getTextPart("oldeb")),
                propriete = objectMapper.readValue<OldebProprieteForm>(httpRequest.getTextPart("propriete")),
                locataire = httpRequest.takeIf { it.getPart("locataire") != null }?.getTextPart("locataire")?.let { objectMapper.readValue<OldebLocataireForm>(it) },
                visiteList = httpRequest.takeIf { it.getPart("visiteList") != null }?.getTextPart("visiteList")?.let { objectMapper.readValue<List<OldebVisiteForm>>(it) },
                documentList = httpRequest.parts.filter { it.name.startsWith("document_") },
            ),
        ).wrap()

    @PUT
    @Path("/{oldebId}")
    @RequireDroits([Droit.OLDEB_U])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun update(
        @Context httpRequest: HttpServletRequest,
        @PathParam("oldebId") oldebId: UUID,
    ): Response {
        val oldeb = objectMapper.readValue<OldebForm>(httpRequest.getTextPart("oldeb"))
        if (oldebId != oldeb.oldebId) {
            return badRequest().build()
        }
        return updateOldebUseCase.execute(
            userInfo = securityContext.userInfo,
            element = OldebFormInput(
                oldeb = oldeb,
                propriete = objectMapper.readValue<OldebProprieteForm>(httpRequest.getTextPart("propriete")),
                locataire = httpRequest.takeIf { it.getPart("locataire") != null }?.getTextPart("locataire")?.let { objectMapper.readValue<OldebLocataireForm>(it) },
                visiteList = httpRequest.takeIf { it.getPart("visiteList") != null }?.getTextPart("visiteList")?.let { objectMapper.readValue<List<OldebVisiteForm>>(it) },
                documentList = httpRequest.parts.filter { it.name.startsWith("document_") },
            ),
        ).wrap()
    }

    @DELETE
    @Path("/{oldebId}")
    @RequireDroits([Droit.OLDEB_D])
    fun delete(@PathParam("oldebId") oldebId: UUID): Response =
        deleteOldebUseCase.execute(
            userInfo = securityContext.userInfo,
            element = oldebRepository.selectOldeb(oldebId),
        ).wrap()

    @GET
    @Path("/caracteristique")
    @Public("Les nomenclatures ne sont pas liées à un droit")
    fun caracteristiqueList(): Response = Response.ok(oldebRepository.getLinkedTypeCaracteristiqueList()).build()

    @GET
    @Path("/anomalie")
    @Public("Les nomenclatures ne sont pas liées à un droit")
    fun anomalieList(): Response = Response.ok(oldebRepository.getLinkedTypeAnomalieList()).build()
}
