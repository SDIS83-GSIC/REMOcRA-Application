package remocra.web.carto

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import remocra.app.DataCacheProvider
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.security.NoCsrf
import remocra.utils.addQueryParameters
import remocra.utils.forbidden
import remocra.utils.notFound
import remocra.web.AbstractEndpoint

@Path("/geoserver")
class GeoserverEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var httpClient: OkHttpClient

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
