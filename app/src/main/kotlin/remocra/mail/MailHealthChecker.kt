package remocra.mail

import jakarta.inject.Inject
import remocra.healthcheck.HealthChecker

class MailHealthChecker @Inject constructor(private val mailService: MailService) : HealthChecker(critical = false) {
    override fun check(): Health {
        return try {
            if (mailService.checkConnection()) Health.Success(null) else Health.Failure(null)
        } catch (e: Exception) {
            Health.Failure(e.message)
        }
    }
}
