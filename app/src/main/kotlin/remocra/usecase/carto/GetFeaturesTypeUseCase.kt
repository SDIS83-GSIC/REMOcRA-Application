package remocra.usecase.carto

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.UriInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import remocra.data.CoucheInfo
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.exception.RemocraResponseException
import remocra.geoserver.GeoserverModule
import remocra.usecase.AbstractUseCase
import remocra.utils.addQueryParameters
import java.util.UUID

class GetFeaturesTypeUseCase : AbstractUseCase() {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var geoserverSettings: GeoserverModule.GeoserverSettings

    @Inject lateinit var layerGroupUrlUseCase: LayerGroupUrlUseCase

    @Inject lateinit var httpClient: OkHttpClient

    fun execute(coucheId: UUID, uriInfo: UriInfo): List<CoucheInfo> {
        val couche = coucheRepository.getCoucheById(coucheId)

        if (couche.coucheNom.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES_NAME_NULL)
        }

        // TODO Pour l'instant on split la coucheNom pour récupérer le workspace, voir si c'est robuste ou non
        val workspace = couche.coucheNom?.split(":")?.firstOrNull()

        val layerNames: List<String?> = try {
            // agrégation
            val response = layerGroupUrlUseCase.execute(coucheId)
            response.layerGroup.publishables?.published?.map { it.name } ?: throw RemocraResponseException(ErrorType.ADMIN_COUCHES_NAME_NULL)
        } catch (_: Exception) {
            // Pas un agrégat => simple couche
            listOf(couche.coucheNom)
        }

        val objectMapper = ObjectMapper()

        return layerNames.map { layerName ->
            val queryParameters = MultivaluedHashMap(uriInfo.queryParameters)
            queryParameters.add("version", "1.1.0")
            queryParameters.add("request", "DescribeFeatureType")
            queryParameters.add("typeName", layerName)
            queryParameters.add("maxFeatures", "1")
            queryParameters.add("outputFormat", "application/json")
            queryParameters.add("exceptions", "application/json")
            queryParameters.add("service", "WFS")

            val request = Request.Builder()
                .get()
                .url(
                    geoserverSettings.url
                        .newBuilder()
                        .let { if (workspace != null) it.addPathSegment(workspace) else it }
                        .addPathSegment("ows")
                        .addQueryParameters(queryParameters)
                        .build(),
                )
                .build()

            httpClient.newCall(request).execute().use { response ->
                CoucheInfo(
                    nomCouche = layerName ?: "",
                    paramsCouche = objectMapper.readTree(response.body()!!.string()),
                )
            }
        }
    }
}
