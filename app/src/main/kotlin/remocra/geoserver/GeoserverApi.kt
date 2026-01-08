package remocra.geoserver

import remocra.geoserver.response.CoucheGeoserver
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GeoserverApi {
    @GET("workspaces/{workspaceName}/layers/{name}.json")
    @Headers("Content-Type: application/json")
    fun getCoucheGeoserver(
        @Path("workspaceName") workspaceName: String,
        @Path("name") name: String,
    ): Call<CoucheGeoserver?>
}
