package remocra.schedule

import com.google.inject.Inject
import com.google.inject.Provider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.util.CronExpression
import org.slf4j.LoggerFactory
import remocra.app.DataCacheProvider
import remocra.auth.UserInfo
import remocra.data.ParametresData
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.log.LogManagerFactory
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import remocra.tasks.SchedulableTaskResults
import java.text.ParseException
import java.util.Date

class SchedulableTasksExecutor
@Inject
constructor(
    private var tasks: Set<SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>>,
    private val logManagerFactory: LogManagerFactory,
    private val parametresProvider: Provider<ParametresData>,
    private val dataCacheProvider: DataCacheProvider,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

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

    private fun SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>.schedule(scheduleTime: CronExpression) {
        this.schedulableTask?.cancel()
        this.schedulableTask = this.launch {
            val now = Date()
            val nextExecution = scheduleTime.getNextValidTimeAfter(now) ?: return@launch
            val millis = nextExecution.time - now.time
            delay(millis)
            // Par défaut c'est l'utilisateur système qui exécute les tâches,
            // on reconstruit son UserInfo car c'est lui qui sera fourni aux useCases appelés dans les traitements.
            val userInfoSysteme = UserInfo()
            val utilisateurSysteme = dataCacheProvider.get().utilisateurSysteme
            userInfoSysteme.utilisateur = utilisateurSysteme
            userInfoSysteme.id = utilisateurSysteme.utilisateurId.toString()
            userInfoSysteme.droits = Droit.entries
            userInfoSysteme.addAttribute("given_name", userInfoSysteme.utilisateur.utilisateurPrenom)

            start(logManagerFactory.create(), userInfoSysteme)
            schedule(scheduleTime)
        }
    }

    private fun getTaskInfo(task: SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>): Task? {
        return parametresProvider.get().mapTasksInfo[task.getType()]
    }
}
