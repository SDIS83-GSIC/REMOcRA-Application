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
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.dfci.GetDfciPanneauUseCase
import remocra.usecase.dfci.UpdateDfciPanneauUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

/**
 * Class Endpoint permettant d'effectuer des requêtes liées aux panneaux du DFCI
 */
@Path("/dfci-panneau")
@Produces(MediaType.APPLICATION_JSON)
class DfciPanneauxEndpoint : AbstractEndpoint() {

    @Inject
    private lateinit var getDfciPanneauUseCase: GetDfciPanneauUseCase

    @Inject
    private lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject
    private lateinit var updateDfciPanneauUseCase: UpdateDfciPanneauUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    /**
     * Endpoint pour récupérer les informations d'un panneau suivant l'id donné
     */
    @GET
    @Path("/{dfciPanneauId}")
    @Public("En attente du tableau de droit")
    fun getPanneauById(@PathParam("dfciPanneauId") dfciPanneauId: UUID): Response =
        Response.ok(
            this.getDfciPanneauUseCase.execute(dfciPanneauId),
        ).build()

    /**
     * Endpoint permettant d'avoir le layer de carte contenant les panneaux
     */
    @GET
    @Path("/layer")
    @Public("En attente du tableau de droit")
    fun layer(@QueryParam("bbox") bbox: String, @QueryParam("srid") srid: String): Response =
        Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.DFCI_PANNEAU,
                securityContext.userInfo,
            ),
        ).build()

    /**
     * Endpoint pour mettre à jour le panneau
     */
    @PUT
    @Path("/update")
    @Public("En attente du tableau de droit")
    fun updatePanneau(dfciPanneau: DfciPanneau): Response =
        Response.ok(
            updateDfciPanneauUseCase.execute(
                securityContext.userInfo,
                DfciPanneau(
                    dfciPanneauId = dfciPanneau.dfciPanneauId,
                    dfciPanneauType = dfciPanneau.dfciPanneauType,
                    dfciPanneauEtat = dfciPanneau.dfciPanneauEtat,
                    dfciPanneauBzero = dfciPanneau.dfciPanneauBzero,
                    dfciPanneauDateGps = dfciPanneau.dfciPanneauDateGps,
                    dfciPanneauPosition = dfciPanneau.dfciPanneauPosition,
                    dfciPanneauEquipement = dfciPanneau.dfciPanneauEquipement,
                    dfciPanneauDfciPisteId = dfciPanneau.dfciPanneauDfciPisteId,
                    dfciPanneauNumPiste = dfciPanneau.dfciPanneauNumPiste,
                    dfciPanneauLibellePiste = dfciPanneau.dfciPanneauLibellePiste,
                    dfciPanneauRemarque = dfciPanneau.dfciPanneauRemarque,
                    dfciPanneauGeometrie = dfciPanneau.dfciPanneauGeometrie,
                    dfciPanneauCode = dfciPanneau.dfciPanneauCode,
                    dfciPanneauVersion = dfciPanneau.dfciPanneauVersion + 1, // On incrémente la version de la ligne à chaque update
                ),
            ),
        ).build()
}
