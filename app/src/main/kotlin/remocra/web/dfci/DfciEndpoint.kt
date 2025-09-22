package remocra.web.dfci

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.locationtech.jts.geom.Geometry
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.DfciRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.dfci.ReceptionTravauxUseCase
import remocra.utils.toGeomFromText
import remocra.web.AbstractEndpoint

@Path("/dfci")
@Produces(MediaType.APPLICATION_JSON)
class DfciEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject
    lateinit var receptionTravauxUseCase: ReceptionTravauxUseCase

    @Inject lateinit var dfciRepository: DfciRepository

    @POST
    @Path("/check")
    @Public("Le carroyage est une donnée publique")
    fun queryCarroyage(searchInput: SearchInput): Response =
        Response.ok(dfciRepository.getCarroyage(searchInput.geometry.toGeomFromText())).build()

    data class SearchInput(
        val geometry: Geometry,
    )

    @PUT
    @Path("/reception-travaux/")
    @RequireDroits([Droit.DFCI_RECEPTRAVAUX_C])
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun depotDeliberationSignalement(
        @Context httpRequest: HttpServletRequest,
    ): Response {
        return Response.ok(
            receptionTravauxUseCase.execute(
                securityContext.userInfo,
                httpRequest.getPart("document"),
            ),
        ).build()
    }
}
