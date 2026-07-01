package remocra.web.pei.import

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.pei.DownloadTemplateImportPeiUseCase
import remocra.usecase.pei.ImportPeiUseCase
import remocra.usecase.pei.MajPositionPeiUseCase
import remocra.utils.DateUtils
import remocra.web.AbstractEndpoint
import java.nio.charset.StandardCharsets

@Path("/maj-positions-pei")
@Produces(MediaType.APPLICATION_JSON)
class ImportPeiEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var importPeiUseCase: ImportPeiUseCase

    @Inject
    lateinit var downloadTemplateImportPeiUseCase: DownloadTemplateImportPeiUseCase

    @Inject
    lateinit var majPositionPeiUseCase: MajPositionPeiUseCase

    @Inject
    lateinit var dateUtils: DateUtils

    @POST
    @Path("/verification")
    @RequireDroits([Droit.PEI_DEPLACEMENT_U])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importPeiVerification(@Context httpRequest: HttpServletRequest): Response =
        Response.ok().entity(
            importPeiUseCase.importPeiValidation(
                httpRequest.getPart("file").inputStream,
                securityContext.userInfo,
            ),
        ).build()

    @POST
    @Path("/enregistrement")
    @RequireDroits([Droit.PEI_DEPLACEMENT_U])
    @Consumes(MediaType.APPLICATION_JSON)
    fun importPeiEnregistrement(importPeiData: ImportPeiData): Response =
        Response.ok(
            majPositionPeiUseCase.execute(
                importPeiData,
                securityContext.userInfo,
            ),
        ).build()

    @POST
    @Path("/download-template")
    @RequireDroits([Droit.PEI_DEPLACEMENT_U])
    @Produces(MediaType.TEXT_PLAIN + "; charset=ISO-8859-1")
    fun exportData(): Response =
        Response.ok(downloadTemplateImportPeiUseCase.execute().toString(StandardCharsets.ISO_8859_1))
            .header("Content-Disposition", "attachment; filename=\"template-${dateUtils.now()}.csv\"")
            .build()
}
