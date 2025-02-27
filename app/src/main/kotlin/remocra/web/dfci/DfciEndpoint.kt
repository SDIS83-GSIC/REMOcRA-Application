package remocra.web.dfci

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.app.AppSettings
import remocra.auth.Public
import remocra.db.DfciRepository
import remocra.db.UtilisateurRepository
import remocra.web.AbstractEndpoint

@Path("/dfci")
@Produces(MediaType.APPLICATION_JSON)
class DfciEndpoint : AbstractEndpoint() {

    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    @Inject lateinit var dfciRepository: DfciRepository

    @Inject lateinit var appSettings: AppSettings

    @POST
    @Path("/check")
    @Public("Le carroyage est une donnée publique")
    fun queryCarroyage(searchInput: SearchInput): Response =
        Response.ok(dfciRepository.getCarroyage(searchInput.x, searchInput.y, searchInput.srid, appSettings.srid)).build()

    data class SearchInput(
        val x: Double,
        val y: Double,
        val srid: Int,
    )
}
