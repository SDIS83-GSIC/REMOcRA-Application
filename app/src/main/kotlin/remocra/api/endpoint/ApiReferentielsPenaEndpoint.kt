package remocra.api.endpoint

import com.fasterxml.jackson.core.JsonProcessingException
import fr.sdis83.remocra.authn.ApiRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.NomenclaturesProvider
import remocra.data.enums.TypeNomenclature
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.web.limitOffset

@Path("deci/referentiel/pena")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsPenaEndpoint {

    @Inject
    lateinit var nomenclaturesProvider: NomenclaturesProvider

    @GET
    @Path("/naturesPEI")
    @Operation(summary = "Retourne les natures de PEI possibles pour les PEI de type PENA", tags = ["DECI - Référentiels PENA"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesPEI(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.NATURE)
                .values
                .filter { TypePei.PENA == (it as Nature).natureTypePei }
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/materiaux")
    @Operation(summary = "Retourne les matériaux possibles pour les PEI de type PENA", tags = ["DECI - Référentiels PENA"])
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielMateriaux(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            nomenclaturesProvider.getData(TypeNomenclature.MATERIAU)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("{codeNature}/naturesAnomalies")
    @Operation(
        summary = "Retourne les types d'anomalies pouvant être constatées pour une nature de PENA et un contexte " +
            "(type de visite) spécifiques",
        tags = ["DECI - Référentiels PENA"],
    )
    @RolesAllowed(ApiRole.RoleType.RECEVOIR)
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesAnomalies(
//        @Parameter(description = "Nature du PIBI") @PathParam("codeNature") codeNature: String?,
//        @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") contexteVisite: String?,
//        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
//        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response {
        TODO()
    }
}
