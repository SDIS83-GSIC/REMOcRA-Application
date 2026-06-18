package remocra.web.dfci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.locationtech.jts.geom.Geometry
import remocra.auth.Public
import remocra.usecase.dfci.GetIdCodeLibelleDfciPistesUseCase
import remocra.web.AbstractEndpoint

/**
 * Classe Endpoint pour les différentes requêtes sur les pistes du module DFCI
 */
@Path("/dfci-pistes")
@Produces(MediaType.APPLICATION_JSON)
class DfciPistesEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getIdCodeLibelleDfciPistesUseCase: GetIdCodeLibelleDfciPistesUseCase

    @GET
    @Path("/piste-id-code-libelle")
    @Public("En attente du tableau de droit")
    fun getPisteIdCodeLibelle(
        @QueryParam("geometrie") geometrie: Geometry,
    ): Response =
        Response.ok(getIdCodeLibelleDfciPistesUseCase.execute(geometrie)).build()
}
