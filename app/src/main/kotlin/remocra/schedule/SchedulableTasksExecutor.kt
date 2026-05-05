package remocra.schedule

import jakarta.inject.Inject
import jakarta.inject.Provider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.core.util.CronExpression
import org.slf4j.LoggerFactory
import remocra.app.DataCacheProvider
import remocra.auth.UserInfo
import remocra.auth.WrappedUserInfo
import remocra.data.ParametresData
import remocra.data.enums.TypeSourceModification
import remocra.db.JobRepository
import remocra.db.LogLineRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.LogLineGravity
import remocra.db.jooq.remocra.tables.pojos.LogLine
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.log.LogManagerFactory
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import remocra.tasks.SchedulableTaskResults
import remocra.utils.DateUtils
import java.text.ParseException
import java.util.Date
import java.util.UUID

class SchedulableTasksExecutor
@Inject
constructor(
    private var tasks: Set<SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>>,
    private val logManagerFactory: LogManagerFactory,
    private val logLineRepository: LogLineRepository,
    private val parametresProvider: Provider<ParametresData>,
    private val dataCacheProvider: DataCacheProvider,
    private val jobRepository: JobRepository,
    private val dateUtils: DateUtils,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() {
        // ici on est dans un redémarrage de l'application / rechargement des données
        // on doit donc mettre en erreur les tâches qui ont été arrété alors qu'elles étaient en cours

        val jobEnCours = jobRepository.getJobsEnCours()
        jobEnCours.forEach {
            // On insère une log line pour avoir l'information
            logLineRepository.writeLogLine(
                LogLine(
                    logLineId = UUID.randomUUID(),
                    logLineJobId = it,
                    logLineGravity = LogLineGravity.ERROR,
                    logLineDate = dateUtils.now(),
                    logLineObjectId = null,
                    logLineMessage = "Le serveur a redémarré ou un rechargement des tâches a eu lieu, ce qui a entraîné un arrêt du traitement.",
                ),
            )
        }

        jobRepository.updateJobEnErreur(jobEnCours)

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
            val userInfoSysteme = UserInfo(
                utilisateur = dataCacheProvider.get().utilisateurSysteme,
                droits = Droit.entries.toSet(),
                zoneCompetence = null,
                affiliatedOrganismeIds = emptySet(),
                groupeFonctionnalites = null,
                typeSourceModification = TypeSourceModification.REMOCRA_WEB,
            )
            // On wrappe dans l'objet qui va bien
            val wrappedUserInfo = WrappedUserInfo()
            wrappedUserInfo.userInfo = userInfoSysteme
            start(logManagerFactory.create(), wrappedUserInfo)
            schedule(scheduleTime)
        }
    }

    private fun getTaskInfo(task: SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>): Task? {
        return parametresProvider.get().mapTasksInfo[task.getType()]
    }
}
