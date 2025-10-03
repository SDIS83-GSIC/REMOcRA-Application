package remocra.web.risque

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.risque.ImportRisqueKmlData
import remocra.db.RisqueExpressRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.risque.DeleteRisquesExpressUseCase
import remocra.usecase.risque.GetRisquesExpressUseCase
import remocra.usecase.risque.ImportRisqueExpressUseCase
import remocra.utils.getTextPartOrNull
import remocra.web.AbstractEndpoint

@Path("/risque")
@Produces(MediaType.APPLICATION_JSON)
class RisqueEndPoint : AbstractEndpoint() {

    @Inject lateinit var importRisqueExpressUseCase: ImportRisqueExpressUseCase

    @Inject lateinit var deleteRisquesExpressUseCase: DeleteRisquesExpressUseCase

    @Inject lateinit var getRisquesExpressUseCase: GetRisquesExpressUseCase

    @Inject lateinit var risqueExpressRepository: RisqueExpressRepository

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var objectMapper: ObjectMapper

    @PUT
    @Path("/import")
    @RequireDroits([Droit.RISQUE_EXPRESS_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importData(
        @Context httpRequest: HttpServletRequest,
    ) =
        importRisqueExpressUseCase.execute(
            securityContext.userInfo,
            ImportRisqueKmlData(
                risqueId = null,
                risqueLibelle = httpRequest.getTextPartOrNull("risqueLibelle"),
                fileKml = if (httpRequest.getPart("fileKml")?.contentType != null) httpRequest.getPart("fileKml").inputStream else null,
            ),
        ).wrap()

    @DELETE
    @Path("/delete")
    @RequireDroits([Droit.RISQUE_EXPRESS_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteRisquesExpress(): Response {
        return deleteRisquesExpressUseCase.execute(securityContext.userInfo, risqueExpressRepository.getRisquesExpress()).wrap()
    }

    @GET
    @Path("/get")
    @Public("Les risques express ne sont pas liées à un droit.")
    @Produces(MediaType.APPLICATION_JSON)
    fun getRisquesExpress(): Response {
        return Response.ok(
            getRisquesExpressUseCase.getRisquesExpress(),
        ).build()
    }
}
