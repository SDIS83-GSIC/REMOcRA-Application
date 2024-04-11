package remocra.keycloak

import com.google.inject.Inject
import remocra.healthcheck.HealthChecker
import remocra.healthcheck.HealthModule
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class KeycloakHealthChecker
@Inject
constructor(settings: HealthModule.HealthSettings, @HealthcheckUrl url: URI) : HealthChecker() {
    private val client: HttpClient
    private val request: HttpRequest

    init {
        this.client = HttpClient.newBuilder().connectTimeout(settings.checkTimeout).build()
        request = HttpRequest.newBuilder(url).build()
        critical = true
    }

    override fun check(): Health {
        val response =
            try {
                client.send(request, HttpResponse.BodyHandlers.ofInputStream())
            } catch (e: Exception) {
                if (e is InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                return Health.Failure(e.message)
            }

        return if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            Health.Success(null)
        } else {
            Health.Failure(response.body())
        }
    }
}
