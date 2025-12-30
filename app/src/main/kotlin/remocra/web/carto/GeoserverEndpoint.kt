package remocra.web.carto

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import remocra.app.DataCacheProvider
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.exception.RemocraResponseException
import remocra.security.NoCsrf
import remocra.usecase.carto.CheckCoucheDispoGeoserverUseCase
import remocra.usecase.carto.GetFeaturesTypeUseCase
import remocra.utils.addQueryParameters
import remocra.utils.forbidden
import remocra.utils.notFound
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/geoserver")
class GeoserverEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var httpClient: OkHttpClient

    @Inject lateinit var getFeaturesTypeUseCase: GetFeaturesTypeUseCase

    @Inject lateinit var checkCoucheDispoGeoserverUseCase: CheckCoucheDispoGeoserverUseCase

    @Public("Les couches peuvent être accessibles publiquement")
    @NoCsrf("OpenLayers utilise un <img src=> qui ne permet pas l'entête CSRF")
    @Path("/describe-feature-type/{coucheId}")
    @GET
    @Throws(RemocraResponseException::class)
    fun describeFeatureType(
        @PathParam("coucheId") coucheId: UUID,
        @Context uriInfo: UriInfo,
    ): Response {
        return try {
            val result = getFeaturesTypeUseCase.execute(coucheId, uriInfo)
            Response
                .ok(result)
                .type(MediaType.APPLICATION_JSON)
                .build()
        } catch (e: RemocraResponseException) {
            Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN)
                .build()
        }
    }

    @Path("/check-couche-dispo/{coucheNom}")
    @RequireDroits([Droit.ADMIN_COUCHE_CARTOGRAPHIQUE])
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    fun getCoucheDispoGeoserver(
        @PathParam("coucheNom") coucheNom: String,
    ): Response {
        return Response.ok(checkCoucheDispoGeoserverUseCase.execute(coucheNom)).build()
    }

    @Public("Les couches peuvent être accessibles publiquement")
    @NoCsrf("OpenLayers utilise un <img src=> qui ne permet pas l'entête CSRF")
    @Path("/{module}/{couche}")
    @GET
    fun proxy(
        @PathParam("module") module: TypeModule,
        @PathParam("couche") code: String,
        @Context uriInfo: UriInfo,
        @Context securityContext: SecurityContext,
    ): Response {
        val user = securityContext.userInfo
        val couche = dataCacheProvider.get().mapCouches.values.firstOrNull {
            it.coucheCode == code && (user!!.isSuperAdmin || it.couchePublic || it.groupeFonctionnalitesList.contains(user.groupeFonctionnalites!!.groupeFonctionnalitesId))
        }

        if (couche == null) {
            // On ne sait pas ici si c'est un problème de droit ou de couche inexistante
            // On ne cherche pas à distinguer les deux
            return notFound().build()
        }

        val queryParameters = MultivaluedHashMap(uriInfo.queryParameters)

        // L'utilisateur n'est pas superadmin ou la couche n'est pas publique, on filtre sur la zone d'intégration
        if (!couche.couchePublic) {
            if (!user.isSuperAdmin) {
                if (user.zoneCompetence == null) {
                    return forbidden().build()
                }
                // XXX : Chaîne en dur, rajouter les noms des propriétés depuis les déclarations jOOQ ?
                queryParameters.add(
                    "CQL_FILTER",
                    "WITHIN(geometrie,(querySingle('remocra:zone_integration','zone_integration_geometrie','zone_integration_id=\'\'${user.zoneCompetence!!.zoneIntegrationId}\'\'')))",
                )
            }
        }

        val url = HttpUrl.get(couche.coucheUrl)
            .newBuilder()
            .addQueryParameters(queryParameters)
            .build()
        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        return doProxyRequest(httpClient, request)
    }
}
