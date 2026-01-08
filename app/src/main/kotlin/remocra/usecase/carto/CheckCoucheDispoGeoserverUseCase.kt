package remocra.usecase.carto

import jakarta.inject.Inject
import remocra.geoserver.GeoserverApi
import remocra.usecase.AbstractUseCase

class CheckCoucheDispoGeoserverUseCase @Inject constructor(
    private val geoserverApi: GeoserverApi,
) : AbstractUseCase() {

    fun execute(coucheNom: String): Boolean {
        coucheNom.split(":").let {
            if (it.size < 2) {
                return false
            }
            val workspace = it[0]
            val name = it[1]

            return geoserverApi.getCoucheGeoserver(workspace, name).execute().body() != null
        }
    }
}
