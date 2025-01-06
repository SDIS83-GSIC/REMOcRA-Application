package remocra.web.importctp

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.importctp.ImportCtpData
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.importctp.ExportCtpUseCase
import remocra.usecase.importctp.ImportCtpUseCase
import remocra.utils.DateUtils
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/importctp")
@Produces(MediaType.APPLICATION_JSON)
class ImportCtpEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var importCtpUseCase: ImportCtpUseCase

    @Inject
    lateinit var exportCtpUseCase: ExportCtpUseCase

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var dateUtils: DateUtils

    @POST
    @Path("/verification")
    @RequireDroits([Droit.IMPORT_CTP_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importCtpVerification(@Context httpRequest: HttpServletRequest): Response =
        Response.ok().entity(
            importCtpUseCase.importCtpValidation(
                httpRequest.getPart("file").inputStream,
                securityContext.userInfo!!,
            ),
        ).build()

    @POST
    @Path("/enregistrement")
    @RequireDroits([Droit.IMPORT_CTP_A])
    @Consumes(MediaType.APPLICATION_JSON)
    fun importCtpEnregistrement(importCtpData: ImportCtpData): Response =
        Response.ok(
            importCtpUseCase.importCtpEnregistrement(
                importCtpData,
                securityContext.userInfo!!,
            ),
        ).build()

    @POST
    @Path("/export")
    @RequireDroits([Droit.IMPORT_CTP_A])
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    fun exportData(paramExportCTPInput: ParamExportCTPInput): Response =
        Response.ok(exportCtpUseCase.execute(paramExportCTPInput.myObject.communeId, securityContext.userInfo!!))
            .header("Content-Disposition", "attachment; filename=\"file.xlsx\"")
            .build()

    class ParamExportCTPInput {
        @FormParam("communeId")
        lateinit var myObject: MyObject
    }
    class MyObject {
        val communeId: UUID? = null
    }
}
