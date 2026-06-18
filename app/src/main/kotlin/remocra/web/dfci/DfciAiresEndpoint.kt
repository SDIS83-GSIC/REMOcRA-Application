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
import remocra.usecase.dfci.GetDfciAiresUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

/**
 * Classe Endpoint pour les différentes requêtes sur les aires du module DFCI
 */
@Path("/dfci-aires")
@Produces(MediaType.APPLICATION_JSON)
class DfciAiresEndpoint : AbstractEndpoint() {

    @Inject
    private lateinit var getDfciAiresUseCase: GetDfciAiresUseCase

    @Inject
    private lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    /**
     * Requête GET afin de récupérer les infos d'une aires via l'id d'une piste
     * @param aireId l'id de l'aire a récupérer
     * @return La réponse de la récupération de l'aire
     */
    @GET
    @Path("/{dfciAireId}")
    @Public("En attente du tableau de droit")
    fun getDfciAireById(
        @PathParam("dfciAireId") aireId: UUID,
    ): Response =
        Response.ok(
            getDfciAiresUseCase.execute(aireId),
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
                TypeElementCarte.DFCI_AIRE,
                securityContext.userInfo,
            ),
        ).build()
}
