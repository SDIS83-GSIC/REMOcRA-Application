package remocra.web.dfci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.dfci.GetDfciDebUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

/**
 * Classe Endpoint pour les différentes requêtes sur les débroussaillements du module DFCI
 */
@Path("/dfci-deb")
@Produces(MediaType.APPLICATION_JSON)
class DfciDebEndpoint : AbstractEndpoint() {

    @Inject
    private lateinit var getDfciDebUseCase: GetDfciDebUseCase

    @Inject
    private lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    @GET
    @Path("/{dfciDebId}")
    @Public("En attente du tableau de droit")
    fun getDebById(@PathParam("dfciDebId") debId: UUID): Response =
        Response.ok(
            getDfciDebUseCase.execute(debId),
        ).build()

    @GET
    @Path("/layer")
    @Public("En attente du tableau de droit")
    fun layer(@QueryParam("bbox") bbox: String, @QueryParam("srid") srid: String): Response =
        Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.DFCI_DEB,
                securityContext.userInfo,
            ),
        ).build()
}
