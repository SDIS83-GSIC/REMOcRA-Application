package remocra.keycloak

import com.google.common.net.HttpHeaders
import remocra.keycloak.representations.ClientRepresentation
import remocra.keycloak.representations.CredentialRepresentation
import remocra.keycloak.representations.UserRepresentation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface KeycloakApi {

    @POST("users")
    @Headers("Content-Type: application/json")
    fun createUser(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Body user: UserRepresentation,
    ): Call<Void?>

    @GET("users")
    fun getUsers(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String,
        @Query("first") first: Int? = null,
        @Query("max") max: Int? = null,
        @Query("username") username: String? = null,
    ): Call<List<UserRepresentation>>

    @PUT("users/{id}/execute-actions-email")
    fun executeActionsEmail(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String,
        @Path("id") userId: String,
        @Query("client_id") clientId: String = "remocra",
        @Body actions: Set<String>,
    ): Call<Void>

    @DELETE("users/{id}")
    @Headers("Content-Type: application/json")
    fun deleteUser(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Path("id") userId: String,
    ): Call<Void>

    @PUT("users/{id}")
    fun updateUser(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String,
        @Path("id") userId: String,
        @Body user: UserRepresentation,
    ): Call<Void>

    // Client
    @POST("clients")
    @Headers("Content-Type: application/json")
    fun createClient(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Body client: ClientRepresentation,
    ): Call<Void?>

    @PUT("clients/{id}")
    @Headers("Content-Type: application/json")
    fun updateClient(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Path("id") techniqueId: String,
        @Body client: ClientRepresentation,
    ): Call<Void?>

    @POST("clients/{id}/client-secret")
    @Headers("Content-Type: application/json")
    fun regenereSecret(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Path("id") techniqueId: String,
    ): Call<CredentialRepresentation>

    @GET("clients/{id}/client-secret")
    @Headers("Content-Type: application/json")
    fun getClientSecret(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Path("id") techniqueId: String,
    ): Call<CredentialRepresentation>

    @GET("clients")
    @Headers("Content-Type: application/json")
    fun getClientRemocraMobile(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String?,
        @Query("clientId") clientId: String = "remocra-mobile",
    ): Call<List<ClientRepresentation>>
}
