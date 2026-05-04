package remocra.geoserver

import jakarta.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import remocra.healthcheck.HealthChecker
import remocra.healthcheck.HealthModule
import java.net.SocketTimeoutException

class GeoserverHealthChecker @Inject constructor(
    settings: HealthModule.HealthSettings,
    baseHttpClient: OkHttpClient,
    geoserverSettings: GeoserverModule.GeoserverSettings,
) : HealthChecker(false) {
    private val httpClient = baseHttpClient.newBuilder()
        .callTimeout(settings.checkTimeout)
        .build()

    // XXX: tester une autre URL plus spécifique ?
    private val checkUrl = geoserverSettings.url

    override fun check(): Health {
        val request = Request.Builder()
            .get()
            .url(checkUrl)
            .build()

        val response = try {
            httpClient.newCall(
                request,
            ).execute()
        } catch (e: SocketTimeoutException) {
            return Health.Timeout
        } catch (e: Exception) {
            if (e is InterruptedException) {
                Thread.currentThread().interrupt()
            }
            return Health.Failure(e.message)
        }

        response.use {
            return if (response.isSuccessful || response.isRedirect) {
                Health.Success(null)
            } else {
                Health.Failure(response.body()?.string())
            }
        }
    }
}
