package remocra.schedule

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.util.CronExpression
import org.jooq.JSONB
import org.slf4j.LoggerFactory
import remocra.tasks.ApacheHopTask
import java.text.ParseException
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration.Companion.milliseconds

/**
 * Cette classe permet d'exécuter les jobs spécifiques.
 * En base, la task doit être de type "PERSONNALISEE" et doit avoir comme paramètre un libelleTask.
 * La gestion des jobs devra être faite dans la moulinette, c'est-à-dire que chaque moulinette devra créer un job (création et insertion des log_line)
 * et gérer son statut (EN_ERREUR, EN_COURS, TERMINE).
 */
@Singleton
class ApacheTasksExecutor
@Inject
constructor(
    private var apacheHopTask: ApacheHopTask,
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val scheduledTasks = ConcurrentHashMap<UUID, Job>()
    private val taskGenerations = ConcurrentHashMap<UUID, AtomicLong>()

    fun start() {
        val tasks = apacheHopTask.getTaskApacheHop()
        val activeTaskIds = tasks.map { it.taskId }.toSet()

        scheduledTasks.keys
            .filter { it !in activeTaskIds }
            .forEach { taskId ->
                scheduledTasks.remove(taskId)?.cancel()
            }

        tasks.forEach { task ->
            try {
                apacheHopTask.schedule(task.taskId, CronExpression(task.taskPlanification), task.taskParametres)
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

    private fun ApacheHopTask.schedule(taskId: UUID, scheduleTime: CronExpression, taskParametres: JSONB?) {
        scheduledTasks.remove(taskId)?.cancel()
        val generation = taskGenerations.computeIfAbsent(taskId) { AtomicLong(0) }.incrementAndGet()

        val apacheHopTask = this
        val scheduledTask = this.launch {
            val now = Date()
            val nextExecution = scheduleTime.getNextValidTimeAfter(now) ?: return@launch
            val millis = nextExecution.time - now.time
            delay(millis.milliseconds)
            if (!isActive) {
                return@launch
            }
            if (taskGenerations[taskId]?.get() != generation) {
                return@launch
            }

            try {
                apacheHopTask.execute(taskParametres!!)
            } catch (e: Exception) {
                logger.error(
                    "Propriété invalide (lancement d'une tâche planifiée) : $this",
                    e,
                )
            }

            if (isActive && taskGenerations[taskId]?.get() == generation) {
                schedule(taskId, scheduleTime, taskParametres)
            }
        }
        scheduledTasks[taskId] = scheduledTask
    }
}
