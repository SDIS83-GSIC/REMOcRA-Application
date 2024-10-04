package remocra.api.endpoint

import com.fasterxml.jackson.core.JsonProcessingException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.app.DataCacheProvider
import remocra.auth.RequireDroitsApi
import remocra.data.enums.TypeDataCache
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.utils.limitOffset
import remocra.web.AbstractEndpoint

@Path("deci/referentiel/pibi")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsPibiEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @GET
    @Path("/naturesPEI")
    @Operation(summary = "Retourne les natures de PEI possibles pour les PEI de type PIBI", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesPEI(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.NATURE)
                .values
                .filter { TypePei.PIBI == (it as Nature).natureTypePei }
                .limitOffset(limit, offset),
        ).build()
    }

    // TODO nature : voir si on a toujours besoin de discriminer les diamètres par nature, et si oui, comment
    // en attendant, on retourne tous les diamètres
    @GET
    @Path("/diametres")
//    @Path("/diametres/{codeNature}")
    @Operation(summary = "Retourne les diamètres de demi-raccord possibles une nature de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielDiametres(
//        @Parameter(description = "Code de nature PIBI") @PathParam("codeNature") codeNature: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.DIAMETRE)
                .values
                .filter { TypePei.PIBI == (it as Nature).natureTypePei }
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("{codeNature}/naturesAnomalies")
    @Operation(
        summary = "Retourne les types d'anomalies pouvant être constatées pour une nature de PIBI et un contexte " +
            "(type de visite) spécifiques",
        tags = ["DECI - Référentiels PIBI"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesAnomalies(
//        @Parameter(description = "Nature du PIBI") @PathParam("codeNature") codeNature: String?,
//        @Parameter(description = "Contexte (code) de la visite") @QueryParam("contexteVisite") contexteVisite: String?,
//        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Int?,
//        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Int?,
    ): Response {
        TODO()
    }

    @GET
    @Path("/marques")
    @Operation(summary = "Retourne les marques susceptibles d'équiper le parc de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielMarques(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.MARQUE_PIBI)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/modeles")
    @Operation(summary = "Retourne les modèles susceptibles d'équiper le parc de PIBI", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielModeles(
        @Parameter(description = "Code de la marque") @QueryParam("codeMarque") codeMarque: String?,
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        @Suppress("UNCHECKED_CAST")
        val data = dataCacheProvider.getData(TypeDataCache.MODELE_PIBI)
            .values
            .let { it as Collection<ModelePibi> }

        // On va chercher l'identifiant de la marque dont le code est passé
        @Suppress("UNCHECKED_CAST")
        val marquePibiId = dataCacheProvider.getData(TypeDataCache.MARQUE_PIBI)
            .values
            .let { it as Collection<MarquePibi> }
            .find { marque -> codeMarque == marque.marquePibiCode }
            ?.marquePibiId
        if (marquePibiId != null) {
            // Si la marque est trouvée, on filtre dessus
            return Response.ok(
                data
                    .filter { it.modelePibiMarqueId == marquePibiId }
                    .limitOffset(limit, offset),
            ).build()
        }
        return Response.ok(
            data
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/typeReseau")
    @Operation(summary = "Retourne le type de réseau", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielTypesReseau(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.TYPE_RESEAU)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/typeCanalisation")
    @Operation(summary = "Retourne le type de canalisation", tags = ["DECI - Référentiels PIBI"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielTypesCanalisation(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.TYPE_CANALISATION)
                .values
                .limitOffset(limit, offset),
        ).build()
    }
}
