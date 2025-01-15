package remocra.keycloak

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provides
import com.google.inject.Singleton
import com.typesafe.config.Config
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import remocra.RemocraModule
import remocra.healthcheck.HealthModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class KeycloakModule(
    private val apiBaseUrl: HttpUrl,
    private val tokenBaseUrl: HttpUrl,
) : RemocraModule() {

    override fun configure() {
        HealthModule.addHealthCheck(binder(), "keycloak").to(KeycloakHealthChecker::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(client: OkHttpClient, mapper: ObjectMapper): Retrofit.Builder {
        // On n'utilise pas toutes les propriétés des objets (UserRepresentation, RoleRepresentation
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
    }

    @Provides
    @Singleton
    fun provideKeycloakApi(retrofit: Retrofit.Builder): KeycloakApi {
        return retrofit.baseUrl(apiBaseUrl).build()
            .create(KeycloakApi::class.java)
    }

    @Provides
    @Singleton
    fun provideKeycloakToken(retrofit: Retrofit.Builder): KeycloakToken {
        return retrofit.baseUrl(tokenBaseUrl).build()
            .create(KeycloakToken::class.java)
    }

    companion object {
        fun create(config: Config): KeycloakModule {
            return KeycloakModule(
                HttpUrl.get(config.getString("base-uri"))
                    .newBuilder()
                    .addPathSegment("admin")
                    .addPathSegment("realms")
                    .addPathSegment(config.getString("realm"))
                    .addPathSegment("") // trailing slash
                    .build(),
                HttpUrl.get(config.getString("base-uri"))
                    .newBuilder()
                    .addPathSegment("realms")
                    .addPathSegment(config.getString("realm"))
                    .addPathSegment("protocol")
                    .addPathSegment("openid-connect")
                    .addPathSegment("") // trailing slash
                    .build(),
            )
        }
    }
}
