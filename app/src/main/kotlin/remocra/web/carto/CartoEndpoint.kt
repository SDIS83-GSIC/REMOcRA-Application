package remocra.web.carto

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Context
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
import remocra.utils.badGateway
import remocra.utils.notFound
import remocra.web.AbstractEndpoint

@Path("/carto")
class CartoEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var httpClient: OkHttpClient

    @Public("Les couches peuvent être accessibles publiquement")
    @NoCsrf("OpenLayers utilise un <img src=> qui ne permet pas l'entête CSRF")
    @GET
    @Path("/{module}/{couche}")
    fun proxy(
        @PathParam("module") module: TypeModule,
        @PathParam("couche") code: String,
        @Context uriInfo: UriInfo,
        @Context securityContext: SecurityContext,
    ): Response {
        val user = securityContext.userInfo
        val couche = if (user == null) {
            dataCacheProvider.get().mapCouches.values.firstOrNull {
                it.coucheCode == code && it.couchePublic
            }
        } else {
            dataCacheProvider.get().mapCouches.values.firstOrNull {
                it.coucheCode == code && (user.isSuperAdmin || it.couchePublic || it.profilDroitList.contains(user.profilDroits!!.profilDroitId))
            }
        }
        if (couche == null) {
            // On ne sait pas ici si c'est un problème de droit ou de couche inexistante
            // On ne cherche pas à distinguer les deux
            return notFound().build()
        }

        val url = HttpUrl.get(couche.coucheUrl)
            .newBuilder()
            .addQueryParameters(uriInfo.queryParameters)
            .build()
        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        return doProxyRequest(httpClient, request)
    }
}

fun doProxyRequest(httpClient: OkHttpClient, request: Request): Response {
    httpClient.newCall(request).execute().use { response ->
        if (response.isRedirect) {
            return badGateway().build()
        }
        return Response.status(response.code())
            .type(response.body()?.contentType()?.toString())
            // XXX: streamer la réponse plutôt que la charger en mémoire entièrement ?
            .entity(response.body()?.bytes())
            .build()
    }
}
