package remocra.cli

import com.google.inject.Inject
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import remocra.http.HttpServer
import remocra.schedule.SchedulableTasksExecutor
import remocra.sentry.SentryService

class Serve
@Inject
constructor(
    private val httpServer: HttpServer,
    private val flyway: Flyway,
    private val sentry: SentryService,
    private val schedulableTasksExecutor: SchedulableTasksExecutor,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        try {
            sentry.start()
            flyway.validate()
            httpServer.start()
            schedulableTasksExecutor.start()
        } catch (exception: Exception) {
            logger.error("Une erreur est survenue au lancement de l'application", exception)
            return
        }

        httpServer.waitTillInterrupt()
    }
}
