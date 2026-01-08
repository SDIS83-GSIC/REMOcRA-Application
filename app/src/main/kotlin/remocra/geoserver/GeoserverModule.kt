package remocra.geoserver

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provides
import com.typesafe.config.Config
import jakarta.inject.Singleton
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import remocra.RemocraModule
import remocra.healthcheck.HealthModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class GeoserverModule(
    private val settings: GeoserverSettings,
    private val apiBaseUrl: HttpUrl,
) : RemocraModule() {
    override fun configure() {
        bind(GeoserverSettings::class.java).toInstance(settings)
        HealthModule.addHealthCheck(binder(), "geoserver").to(GeoserverHealthChecker::class.java)
    }

    companion object {
        fun create(config: Config): GeoserverModule = GeoserverModule(
            settings = GeoserverSettings(
                url = HttpUrl.get(config.getString("url")),
                username = config.getString("username"),
                password = config.getString("password"),
            ),
            apiBaseUrl =
            HttpUrl.get(config.getString("url"))
                .newBuilder()
                .addPathSegment("rest")
                .addPathSegment("") // slash pour Retrofit
                .build(),
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader("Authorization", Credentials.basic(settings.username, settings.password))
                        .addHeader("Accept", "application/json")
                        .build(),
                )
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideGeoserverApi(
        retrofitBuilder: Retrofit.Builder,
        mapper: ObjectMapper,
        okHttpClient: OkHttpClient,
    ): GeoserverApi {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        return retrofitBuilder
            .baseUrl(apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(GeoserverApi::class.java)
    }

    data class GeoserverSettings(val url: HttpUrl, val username: String, val password: String)
}
