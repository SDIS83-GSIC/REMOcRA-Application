package remocra.schedule

import com.google.inject.Inject
import com.google.inject.Provider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.util.CronExpression
import org.slf4j.LoggerFactory
import remocra.data.ParametresData
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.log.LogManagerFactory
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import java.text.ParseException
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SchedulableTasksExecutor
@Inject
constructor(
    private var tasks: Set<SchedulableTask<out SchedulableTaskParameters>>,
    private val logManagerFactory: LogManagerFactory,
    private val parametresProvider: Provider<ParametresData>,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val parser: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("H[:mm]", Locale.getDefault())

    fun start() {
        tasks.forEach { task ->
            try {
                if (getTaskInfo(task)?.taskPlanification != null) {
                    task.schedule(CronExpression(getTaskInfo(task)?.taskPlanification))
                }
            } catch (pe: ParseException) {
                logger.error("Erreur lors du parsing de la date de lancement de la tâche", pe)
            } catch (e: Exception) {
                logger.error(
                    "Propriété invalide (lancement d'une tâche planifiée) : ${task.getType()}",
                    e,
                )
            }
        }
    }

    private fun SchedulableTask<out SchedulableTaskParameters>.schedule(scheduleTime: CronExpression) {
        this.launch {
            val now = Date()
            val nextExecution = scheduleTime.getNextValidTimeAfter(now) ?: return@launch
            val millis = nextExecution.time - now.time
            delay(millis)
            start(logManagerFactory.create())
            schedule(scheduleTime)
        }
    }

    private fun getTaskInfo(task: SchedulableTask<out SchedulableTaskParameters>): Task? {
        return parametresProvider.get().mapTasksInfo[task.getType()]
    }
}
