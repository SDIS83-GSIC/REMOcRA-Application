package remocra.keycloak

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.AuthModule
import remocra.healthcheck.HealthChecker
import remocra.healthcheck.HealthModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class KeycloakHealthChecker
@Inject
constructor(
    private val settings: HealthModule.HealthSettings,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
) : HealthChecker(critical = true) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val revokeTokenCallback = object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            logger.error("Erreur à la révocation du token obtenu lors du healthcheck", t)
        }
    }

    override fun check(): Health {
        val getTokenCall = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret)
        getTokenCall.timeout().timeout(settings.checkTimeout.toNanos(), TimeUnit.NANOSECONDS)
        val response =
            try {
                getTokenCall.execute()
            } catch (e: SocketTimeoutException) {
                return Health.Timeout
            } catch (e: Exception) {
                if (e is InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                return Health.Failure(e.message)
            }
        if (response.isSuccessful) {
            response.body()?.accessToken?.also {
                keycloakToken.revokeToken(it, keycloakClient.clientId, keycloakClient.clientSecret)
                    .enqueue(revokeTokenCallback)
            }
            return Health.Success(null)
        } else {
            return Health.Failure(response.errorBody()!!.string())
        }
    }
}
