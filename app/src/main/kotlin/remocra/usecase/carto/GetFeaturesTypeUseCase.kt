package remocra.usecase.carto

import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.UriInfo
import okhttp3.Request
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

    fun execute(coucheId: UUID, uriInfo: UriInfo): Request {
        val couche = coucheRepository.getCoucheById(coucheId)

        if (couche.coucheNom.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.ADMIN_COUCHES_NAME_NULL)
        }
        val queryParameters = MultivaluedHashMap(uriInfo.queryParameters)

        queryParameters.add("version", "1.1.0")
        queryParameters.add("request", "DescribeFeatureType")
        queryParameters.add("typeName", couche.coucheNom)
        queryParameters.add("maxFeatures", "1")
        queryParameters.add("outputFormat", "application/json")
        queryParameters.add("exceptions", "application/json")
        queryParameters.add("service", "WFS")
        // TODO Pour l'instant on split la coucheNom pour récupérer le workspace, voir si c'est robuste ou non
        val workspace = couche.coucheNom?.split(":")?.firstOrNull()

        val url = geoserverSettings.url
            .newBuilder()
            .let { if (workspace != null) it.addPathSegment(workspace) else it }
            .addPathSegment("ows")
            .addQueryParameters(queryParameters)
            .build()
        return Request.Builder()
            .get()
            .url(url)
            .build()
    }
}
