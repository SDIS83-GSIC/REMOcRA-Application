package remocra.web.debitsimultane

import com.google.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.enums.TypePointCarte
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.utils.forbidden
import remocra.web.AbstractEndpoint

@Path("/debit-simultane")
@Produces(MediaType.APPLICATION_JSON)
class DebitSimultaneEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Context
    lateinit var securityContext: SecurityContext

    /**
     * Renvoie les débits simultanés au format GeoJSON pour assurer les interactions sur la carte
     */
    @GET
    @Path("/layer")
    @RequireDroits([Droit.DEBITS_SIMULTANES_R])
    fun layer(
        @QueryParam("bbox") bbox: String,
        @QueryParam("srid") srid: String,
    ): Response {
        if (securityContext.userInfo == null) {
            return forbidden().build()
        }
        return Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypePointCarte.DEBIT_SIMULTANE,
                securityContext.userInfo!!,
            ),
        ).build()
    }
}
