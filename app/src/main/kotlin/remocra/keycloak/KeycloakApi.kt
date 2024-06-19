package remocra.keycloak

import com.google.common.net.HttpHeaders
import remocra.keycloak.representations.UserRepresentation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface KeycloakApi {

    @GET("users")
    fun getUsers(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String,
        @Query("first") first: Int? = null,
        @Query("max") max: Int? = null,
        @Query("username") username: String? = null,
    ): Call<List<UserRepresentation>>

    @GET("roles/{role-name}/users")
    fun getUsersInactif(
        @Header(HttpHeaders.AUTHORIZATION) authorization: String,
        @Path("role-name") roleName: String = "inactif",
    ): Call<List<UserRepresentation>>
}
