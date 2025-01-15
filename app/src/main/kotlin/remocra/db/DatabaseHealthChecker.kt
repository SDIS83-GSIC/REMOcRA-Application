package remocra.db

import jakarta.inject.Inject
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import remocra.healthcheck.HealthChecker

class DatabaseHealthChecker @Inject constructor(private val flyway: Flyway) : HealthChecker(critical = true) {
    override fun check(): Health {
        return try {
            flyway.validate()
            Health.Success(null)
        } catch (e: FlywayException) {
            Health.Failure(e.errorCode)
        }
    }
}
