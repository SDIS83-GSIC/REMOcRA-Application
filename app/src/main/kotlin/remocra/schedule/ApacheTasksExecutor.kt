package remocra.schedule

import com.google.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.util.CronExpression
import org.jooq.JSONB
import org.slf4j.LoggerFactory
import remocra.tasks.ApacheHopTask
import java.text.ParseException
import java.util.Date

/**
 * Cette classe permet d'exécuter les jobs spécifiques.
 * En base, la task doit être de type "PERSONNALISEE" et doit avoir comme paramètre un libelleTask.
 * La gestion des jobs devra être faite dans la moulinette, c'est-à-dire que chaque moulinette devra créer un job (création et insertion des log_line)
 * et gérer son statut (EN_ERREUR, EN_COURS, TERMINE)
 */
class ApacheTasksExecutor
@Inject
constructor(
    private var apacheHopTask: ApacheHopTask,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        apacheHopTask.getTaskApacheHop().forEach { task ->
            try {
                apacheHopTask.schedule(CronExpression(task.taskPlanification), task.taskParametres)
            } catch (pe: ParseException) {
                logger.error("Erreur lors du parsing de la date de lancement de la tâche", pe)
            } catch (e: Exception) {
                logger.error(
                    "Propriété invalide (lancement d'une tâche planifiée) : $task",
                    e,
                )
            }
        }
    }

    private fun ApacheHopTask.schedule(scheduleTime: CronExpression, taskParametres: JSONB?) {
        this.schedulableTask?.cancel()

        val apacheHopTask = this
        this.schedulableTask = this.launch {
            val now = Date()
            val nextExecution = scheduleTime.getNextValidTimeAfter(now) ?: return@launch
            val millis = nextExecution.time - now.time
            delay(millis)

            try {
                apacheHopTask.execute(taskParametres!!)
            } catch (e: Exception) {
                logger.error(
                    "Propriété invalide (lancement d'une tâche planifiée) : $this",
                    e,
                )
            }

            schedule(scheduleTime, taskParametres)
        }
    }
}
