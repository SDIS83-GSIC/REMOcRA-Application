package remocra.usecase.carto

import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.UriInfo
import okhttp3.Request
import remocra.db.CoucheRepository
import remocra.geoserver.GeoserverModule
import remocra.usecase.AbstractUseCase
import remocra.utils.addQueryParameters
import java.util.UUID

class GetFeaturesTypeUseCase : AbstractUseCase() {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var geoserverSettings: GeoserverModule.GeoserverSettings

    fun execute(coucheId: UUID, uriInfo: UriInfo): Request {
        val couche = coucheRepository.getCoucheById(coucheId)
        val queryParameters = MultivaluedHashMap(uriInfo.queryParameters)

        queryParameters.add("version", "1.1.0")
        queryParameters.add("request", "DescribeFeatureType")
        queryParameters.add("typeName", couche.coucheNom)
        queryParameters.add("maxFeatures", "1")
        queryParameters.add("outputFormat", "application/json")
        queryParameters.add("exceptions", "application/json")
        queryParameters.add("service", "WFS")

        val url = geoserverSettings.url
            .newBuilder()
            .addPathSegment("ows")
            .addQueryParameters(queryParameters)
            .build()
        return Request.Builder()
            .get()
            .url(url)
            .build()
    }
}
