package remocra.keycloak

import remocra.keycloak.representations.InfosToken
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KeycloakToken {

    @POST("token")
    @FormUrlEncoded
    fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials",
    ): Call<InfosToken>

    @POST("revoke")
    @FormUrlEncoded
    fun revokeToken(
        @Field("token") token: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
    ): Call<Void>
}
