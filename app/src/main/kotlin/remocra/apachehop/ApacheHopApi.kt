package remocra.apachehop

import remocra.apachehop.data.ApacheHopWorflow
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApacheHopApi {

    @GET("/hop/asyncRun")
    @Headers("Content-Type: application/json")
    fun run(
        @Query("service") task: String,
    ): Call<ApacheHopWorflow?>

    @GET("/")
    fun ping(): Call<Void>?
}
