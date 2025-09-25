package remocra.apachehop

import jakarta.inject.Inject
import remocra.healthcheck.HealthChecker

class ApacheHopHealthChecker
@Inject
constructor(private val apacheHopApi: ApacheHopApi) : HealthChecker(critical = false) {
    override fun check(): Health {
        return try {
            apacheHopApi.ping()?.run {
                if (execute().isSuccessful) {
                    Health.Success(null)
                } else
                    Health.Failure(null)
            } ?: Health.Disabled
        } catch (e: Exception) {
            Health.Failure(e.message)
        }
    }
}
