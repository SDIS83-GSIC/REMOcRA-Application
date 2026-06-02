package remocra.apimobile.endpoint

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.apimobile.usecase.ExportLogUseCase
import remocra.auth.Public
import remocra.web.AbstractEndpoint

@Path("/mobile/log")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class MobileLogEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var exportLogUseCase: ExportLogUseCase

    @Path("/export")
    @POST
    @Public("Point d'entrée pour exporter les logs de l'application mobile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun export(
        @Context httpServletRequest: HttpServletRequest,
    ): Response {
        val fichierLogBytes = httpServletRequest.getPart("file").inputStream.use { it.readAllBytes() }
        exportLogUseCase.execute(
            tabletteId = httpServletRequest.getPart("tabletteId").inputStream.reader().readText(),
            fichierLogBytes = fichierLogBytes,
        )
        return Response.ok().build()
    }
}
