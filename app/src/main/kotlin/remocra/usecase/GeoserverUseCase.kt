package remocra.usecase

import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.UriInfo
import okhttp3.Request
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.CoucheData
import remocra.geoserver.GeoserverModule
import remocra.utils.addQueryParameters
import kotlin.collections.contains

class GeoserverUseCase @Inject constructor(
    private val dataCacheProvider: DataCacheProvider,
    private val geoserverSettings: GeoserverModule.GeoserverSettings,
) : AbstractUseCase() {

    fun proxyWms(user: WrappedUserInfo, uriInfo: UriInfo, geoserverPath: String? = null): Result {
        val queryParameters = MultivaluedHashMap(uriInfo.queryParameters)
        // on récupère le type de requête que c'est
        val requestType = queryParameters.getFirstIgnoreCase("REQUEST")?.lowercase()

        // on va chercher la ou les layers
        val layers = queryParameters.getFirstIgnoreCase("LAYERS")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        // Si c'est un de ces 3 là, on doit vérifier les droits d'accès, auquel cas, on ne renvoie une liste vide
        val needsLayerAuthorization = requestType in setOf("getmap", "getfeatureinfo", "getfeature")
        val couches = if (needsLayerAuthorization) {
            val mapped = resolveRequestedLayers(layers)
            if (mapped.size != layers.size) {
                return Result.NotFound("Aucune couche demandée trouvée")
            }
            mapped
        } else {
            emptyList()
        }

        if (needsLayerAuthorization && !hasAccessToCouche(user, couches)) {
            return Result.Forbidden("Vous n'avez pas les droits pour accéder à cette couche")
        }

        val hasPrivateLayer = couches.any { !it.couchePublic }
        val utilisateurGroupeFonctionnaliteId = user.groupeFonctionnalites?.groupeFonctionnalitesId

        val inZcLayer = utilisateurGroupeFonctionnaliteId != null && couches.any {
            !it.couchePublic && it.groupeFonctionnalitesListInZC.contains(utilisateurGroupeFonctionnaliteId)
        }

        if (needsLayerAuthorization && hasPrivateLayer && !user.isSuperAdmin && user.zoneCompetence == null) {
            return Result.Forbidden("Vous n'avez pas les droits pour accéder aux couches : $couches")
        }

        if (needsLayerAuthorization && inZcLayer && !user.isSuperAdmin) {
            val ziId = user.zoneCompetence!!.zoneIntegrationId
            val within = "WITHIN(geometrie,(querySingle('remocra:zone_integration','zone_integration_geometrie','zone_integration_id=''$ziId'')))"
            val existing = queryParameters.getFirstIgnoreCase("CQL_FILTER")
            val merged = if (existing.isNullOrBlank()) within else "$within and ($existing)"
            queryParameters.remove("CQL_FILTER")
            queryParameters.remove("cql_filter")
            queryParameters.putSingle("CQL_FILTER", List(layers.size.coerceAtLeast(1)) { merged }.joinToString(";"))
        }

        val url = geoserverSettings.url
            .newBuilder()
            .apply {
                // Si un suffixe est fourni (dans le path), on le propage tel quel vers GeoServer.
                if (geoserverPath.isNullOrBlank()) {
                    addPathSegment("remocra")
                    addPathSegment("wms")
                } else {
                    geoserverPath
                        .trim('/')
                        .split("/")
                        .filter { it.isNotBlank() }
                        .forEach { addPathSegment(it) }
                }
            }
            .addQueryParameters(queryParameters)
            .build()

        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        return Result.Success(request)
    }

    private fun hasAccessToCouche(user: WrappedUserInfo, couches: List<CoucheData>): Boolean {
        if (user.isSuperAdmin) return true
        if (couches.all { it.couchePublic }) return true
        if (user.groupeFonctionnalites?.groupeFonctionnalitesId == null) return false
        return couches.all {
            it.couchePublic ||
                it.groupeFonctionnalitesListInZC.contains(user.groupeFonctionnalites?.groupeFonctionnalitesId) ||
                it.groupeFonctionnalitesListHorsZc.contains(user.groupeFonctionnalites?.groupeFonctionnalitesId)
        }
    }

    private fun resolveRequestedLayers(requestedLayers: List<String>): List<CoucheData> {
        val allCouches = dataCacheProvider.get().mapCouches.values.toList()
        return requestedLayers.mapNotNull { requestedLayer ->
            allCouches.firstOrNull { it.coucheNom == requestedLayer }
                ?: run {
                    val requestedLocalName = requestedLayer.substringAfterLast(":")
                    allCouches.firstOrNull { it.coucheNom?.substringAfterLast(":") == requestedLocalName }
                }
        }
    }

    private fun MultivaluedHashMap<String, String>.getFirstIgnoreCase(name: String): String? {
        return entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
    }
}
