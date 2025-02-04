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
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class ApacheHopModule(
    private val apiBaseUrl: HttpUrl,
    private val username: String,
    private val password: String,
) : RemocraModule() {

    @Provides
    @Singleton
    fun provideApacheHopApi(retrofit: Retrofit.Builder, mapper: ObjectMapper): ApacheHopApi {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val client = OkHttpClient.Builder()
            .authenticator({ route, response ->
                val credential: String = Credentials.basic(username, password)
                response.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
            })
            .build()
        return retrofit.baseUrl(apiBaseUrl).client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
            .create(ApacheHopApi::class.java)
    }

    companion object {
        fun create(config: Config): ApacheHopModule {
            return ApacheHopModule(
                HttpUrl.get(config.getString("url")),
                config.getString("password"),
                config.getString("username"),
            )
        }
    }
}
