package remocra.log

import org.slf4j.LoggerFactory
import remocra.db.LogLineRepository
import remocra.db.jooq.remocra.enums.LogLineGravity
import remocra.db.jooq.remocra.tables.pojos.LogLine
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Logger permettant d'insérer des informations en base, en quasi-synchrone, mais dans une
 * transaction séparée. <br />
 *
 * A utiliser dans les traitements longs nécessitant un feedback à l'utilisateur au travers d'une
 * IHM
 */
class LogManager(
    private val logLineRepository: LogLineRepository,
    private val clock: Clock,
    val idJob: UUID,
) {

    // Logger inception
    private val logger = LoggerFactory.getLogger(javaClass)

    fun info(message: String, objectId: UUID? = null) {
        writeLog(LogLineGravity.INFO, objectId, message)
    }

    fun warn(message: String, objectId: UUID? = null) {
        writeLog(LogLineGravity.WARN, objectId, message)
    }

    fun error(message: String, objectId: UUID? = null) {
        writeLog(LogLineGravity.ERROR, objectId, message)
    }

    private fun writeLog(gravity: LogLineGravity, objectId: UUID?, message: String) {
        val logLine =
            LogLine(
                UUID.randomUUID(),
                idJob,
                gravity,
                ZonedDateTime.now(clock),
                objectId,
                message,
            )

        try {
            // On lance dans un thread séparé pour obliger jooq à créer un autre transaction.
            Thread { logLineRepository.writeLogLine(logLine) }.start()
        } catch (e: Exception) {
            logger.error("Problème d'insertion d'une logLine : $e")
        }
    }
}
