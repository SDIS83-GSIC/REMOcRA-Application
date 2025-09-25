package remocra.apachehop

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provides
import com.google.inject.Singleton
import com.typesafe.config.Config
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import remocra.RemocraModule
import remocra.apachehop.data.ApacheHopWorflow
import remocra.healthcheck.HealthModule
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class ApacheHopModule private constructor(
    private val apacheHopSettings: ApacheHopSettings? = null,
) : RemocraModule() {

    private data class ApacheHopSettings(
        val apiBaseUrl: HttpUrl,
        val username: String,
        val password: String,
    )

    override fun configure() {
        HealthModule.addHealthCheck(binder(), "apache-hop").to(ApacheHopHealthChecker::class.java)
    }

    @Provides
    @Singleton
    fun provideApacheHopApi(retrofit: Retrofit.Builder, mapper: ObjectMapper): ApacheHopApi {
        if (apacheHopSettings == null) {
            return object : ApacheHopApi {
                override fun run(task: String): Call<ApacheHopWorflow?> {
                    throw RuntimeException("Impossible d'utiliser Apache Hop: il n'est pas activé.")
                }

                override fun ping(): Call<Void>? {
                    return null
                }
            }
        }

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val client = OkHttpClient.Builder()
            .authenticator { route, response ->
                val credential: String = Credentials.basic(apacheHopSettings.username, apacheHopSettings.password)
                response.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
            }
            .build()
        return retrofit.baseUrl(apacheHopSettings.apiBaseUrl).client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
            .create(ApacheHopApi::class.java)
    }

    companion object {
        fun create(config: Config): ApacheHopModule {
            return if (config.getBoolean("enable")) {
                ApacheHopModule(
                    ApacheHopSettings(
                        HttpUrl.get(config.getString("url")),
                        config.getString("password"),
                        config.getString("username"),
                    ),
                )
            } else {
                ApacheHopModule()
            }
        }
    }
}
