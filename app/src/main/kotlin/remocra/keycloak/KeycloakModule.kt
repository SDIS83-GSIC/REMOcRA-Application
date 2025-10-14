package remocra.keycloak

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provides
import com.typesafe.config.Config
import jakarta.inject.Singleton
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import remocra.RemocraModule
import remocra.healthcheck.HealthModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class KeycloakModule(
    private val apiBaseUrl: HttpUrl,
    private val tokenBaseUrl: HttpUrl,
    private val baseUriMobile: HttpUrl,
) : RemocraModule() {

    override fun configure() {
        bind(KeycloakUri::class.java).toInstance(KeycloakUri(baseUriMobile.uri().toString()))
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
    fun provideKeycloakApi(retrofit: Retrofit.Builder, mapper: ObjectMapper): KeycloakApi {
        // On n'utilise pas toutes les propriétés des objets (UserRepresentation, RoleRepresentation
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        return retrofit.baseUrl(apiBaseUrl).addConverterFactory(JacksonConverterFactory.create(mapper)).build()
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
                HttpUrl.get(config.getString("base-uri"))
                    .newBuilder()
                    .addPathSegment("realms")
                    .addPathSegment(config.getString("realm"))
                    .addPathSegment(".well-known")
                    .addPathSegment("openid-configuration")
                    .addPathSegment("") // trailing slash
                    .build(),
            )
        }
    }
}

data class KeycloakUri(
    val baseUri: String,
)
