package remocra.web.dfci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.dfci.GetDfciAiresUseCase
import remocra.usecase.dfci.UpdateAireUseCase
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
    private lateinit var updateAireUseCase: UpdateAireUseCase

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
    @RequireDroits([Droit.DFCI_R])
    fun getDfciAireById(
        @PathParam("dfciAireId") aireId: UUID,
    ): Response =
        Response.ok(
            getDfciAiresUseCase.execute(aireId),
        ).build()

    @GET
    @Path("/layer")
    @RequireDroits([Droit.DFCI_R])
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

    /**
     * Endpoint afin de mettre à jour l'aire passée par la requête
     */
    @PUT
    @Path("/update")
    @RequireDroits([Droit.DFCI_AIRE_U])
    fun updateAireById(dfciAire: DfciAire): Response =
        Response.ok(
            this.updateAireUseCase.execute(
                securityContext.userInfo,
                DfciAire(
                    dfciAireId = dfciAire.dfciAireId,
                    dfciAireAmenagement = dfciAire.dfciAireAmenagement,
                    dfciAireDateGps = dfciAire.dfciAireDateGps,
                    dfciAireGrandeDimension = dfciAire.dfciAireGrandeDimension,
                    dfciAirePetiteDimension = dfciAire.dfciAirePetiteDimension,
                    dfciAireType = dfciAire.dfciAireType,
                    dfciAireDfciPisteId = dfciAire.dfciAireDfciPisteId,
                    dfciAireGeometrie = dfciAire.dfciAireGeometrie,
                    dfciAireRemarque = dfciAire.dfciAireRemarque,
                    dfciAireCode = dfciAire.dfciAireCode,
                    dfciAireVersion = dfciAire.dfciAireVersion + 1, // On incrémente la version de la ligne à chaque update
                ),
            ),
        ).build()
}
