package remocra.geoserver

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.db.CoucheRepository
import java.util.UUID

@Path("/layers")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class LayersEndpoint {
    @Context
    lateinit var securityContext: SecurityContext

    @Inject
    lateinit var layersRetriever: LayersRetriever

    @Inject
    lateinit var coucheRepository: CoucheRepository

    @Public("Les couches peuvent être accessibles publiquement")
    @GET
    fun getLayers(): Response =
        Response.ok(layersRetriever.getData(securityContext.userInfo)).build()

    @Public("Les couches peuvent être accessibles publiquement")
    @GET
    @Path("/{idCouche}/icone")
    @Produces("image/*")
    fun getIcone(@PathParam("idCouche") idCouche: UUID): Response =
        Response.ok().entity(coucheRepository.getIcone(idCouche)).build()

    @Public("Les couches peuvent être accessibles publiquement")
    @GET
    @Path("/{idCouche}/legende")
    @Produces("image/*")
    fun getLegende(@PathParam("idCouche") idCouche: UUID): Response =
        Response.ok().entity(coucheRepository.getLegende(idCouche)).build()
}
