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
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.utils.limitOffset
import remocra.web.AbstractEndpoint

@Path("/deci/referentiel")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class ApiReferentielsDeciEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @GET
    @Path("/naturesDECI")
    @Operation(summary = "Retourne les types de DECI applicables sur les PEI (publique, privée, privée sous convention). Attention la nature DECI peut être différente du domaine", tags = ["DECI - Référentiels communs"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielNaturesDECI(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.NATURE_DECI)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/niveaux")
    @Operation(summary = "Retourne les valeurs de positionnement par rapport au sol possibles pour un PEI", tags = ["DECI - Référentiels communs"])
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getReferentielNiveaux(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.NIVEAU)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/domaines")
    @Operation(
        summary = "Retourne les natures domaniales des terrains sur lesquels les PEI sont localisés " +
            "(domanial, privé, militaire, etc.). Attention la nature du domaine peut être différente de " +
            "la nature de la DECI",
        tags = ["DECI - Référentiels communs"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielDomaines(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            dataCacheProvider.getData(TypeDataCache.DOMAINE)
                .values
                .limitOffset(limit, offset),
        ).build()
    }

    @GET
    @Path("/typesVisites")
    @Operation(
        summary =
        """
            Retourne les types de visites possibles sur les PEI. Ces types sont définis par type d'organisme. Un service des eaux ne pourra par exemple pas renseigner des visites de type Contrôle Technique Périodique (CTP) ou Reconnaissance (ROI ou ROP)  mais pourra renseigner des visites non programmées (NP). Les types de visites déterminent également la liste des anomalies pouvant être constatées sur une nature de PEI
        """,
        tags = ["DECI - Référentiels communs"],
    )
    @RequireDroitsApi([DroitApi.RECEVOIR])
    @Throws(JsonProcessingException::class)
    fun getRefentielVisites(
        @Parameter(description = "Nombre maximum de résultats à retourner") @QueryParam("limit") limit: Long?,
        @Parameter(description = "Retourne les informations à partir de la n-ième ligne") @QueryParam("offset") offset: Long?,
    ): Response {
        return Response.ok(
            TypeVisite.entries.limitOffset(limit, offset),
        ).build()
    }
}
