package remocra.web.risque

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.risque.ImportRisqueKmlData
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.risque.ImportRisqueKmlUseCase
import remocra.utils.getTextPartOrNull
import remocra.web.AbstractEndpoint

@Path("/risque")
@Produces(MediaType.APPLICATION_JSON)
class RisqueEndPoint : AbstractEndpoint() {

    @Inject lateinit var importRisqueKmlUseCase: ImportRisqueKmlUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var objectMapper: ObjectMapper

    @PUT
    @Path("/import")
    @RequireDroits([Droit.RISQUE_KML_A])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun importData(
        @Context httpRequest: HttpServletRequest,
    ) =
        importRisqueKmlUseCase.execute(
            securityContext.userInfo,
            ImportRisqueKmlData(
                risqueId = null,
                risqueLibelle = httpRequest.getTextPartOrNull("risqueLibelle"),
                fileKml = if (httpRequest.getPart("fileKml")?.contentType != null) httpRequest.getPart("fileKml").inputStream else null,
            ),
        ).wrap()
}
