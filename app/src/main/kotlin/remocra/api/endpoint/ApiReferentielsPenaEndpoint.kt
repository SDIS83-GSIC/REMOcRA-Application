package remocra.api.endpoint

import com.fasterxml.jackson.core.JsonProcessingException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.api.usecase.ApiAnomalieNatureUseCase
import remocra.app.DataCacheProvider
import remocra.auth.RequireDroitsApi
import remocra.data.enums.TypeDataCache
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.enums.TypePei
import remocra.utils.limitOffset
import remocra.web.AbstractEndpoint

@Path("deci/referentiel/pena")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsPenaEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var apiAnomalieWithNatureUseCase: ApiAnomalieNatureUseCase

    @GET
    @Path("/naturesPEI")
    @Operation(summary = "Retourne les natures de PEI possibles pour les PEI de type PENA", tags = ["DECI - Référentiels PENA"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesPEI(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.NATURE_PENA)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/materiaux")
    @Operation(summary = "Retourne les matériaux possibles pour les PEI de type PENA", tags = ["DECI - Référentiels PENA"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielMateriaux(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.MATERIAU)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("{natureCode}/naturesAnomalies")
    @Operation(
        summary = "Retourne les types d'anomalies pouvant être constatées pour une nature de PENA et un " +
            "type de visite spécifiques",
        tags = ["DECI - Référentiels PENA"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesAnomalies(
        @Parameter(description = "Nature du PENA") @PathParam("natureCode") natureCode: String,
        @Parameter(description = "Type de la visite") @QueryParam("typeVisite") typeVisite: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response =
        Response.ok(apiAnomalieWithNatureUseCase.execute(natureCode, typeVisite, TypePei.PENA, limit, offset)).build()
}
