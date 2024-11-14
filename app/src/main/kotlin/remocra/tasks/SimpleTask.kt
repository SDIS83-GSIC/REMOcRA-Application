package remocra.tasks

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import com.google.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jooq.JSONB
import remocra.app.AppSettings
import remocra.auth.UserInfo
import remocra.data.ParametresData
import remocra.data.enums.Environment
import remocra.db.JobRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.eventbus.EventBus
import remocra.eventbus.notification.NotificationEvent
import remocra.log.LogManager
import remocra.usecase.tasks.TaskUseCase
import remocra.utils.DateUtils
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Classe de base des tâches, correspondant à la définition d'un travail à effectuer sous forme de
 * [Job]
 */
abstract class SimpleTask<T : TaskParameters, U : JobResults> : CoroutineScope {

    @Inject
    private lateinit var jobRepository: JobRepository

    @Inject
    lateinit var transactionManager: TransactionManager

    @Inject
    lateinit var settings: AppSettings

    @Inject
    lateinit var objectMapper: ObjectMapper

    protected lateinit var logManager: LogManager

    @Inject
    lateinit var parametresProvider: Provider<ParametresData>

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var taskUseCase: TaskUseCase

    @Inject
    lateinit var dateUtils: DateUtils

    /**
     * Méthode exécutant le code de la tâche à proprement parler.
     * Doit être fait de manière bloquante, pas d'appels asynchrones, car la notification est faite dans la foulée
     */
    protected abstract fun execute(parameters: T?, userInfo: UserInfo): U?

    /** Environnements autorisés à exécuter la tâche : par défaut, tous. A overrider au besoin */
    open fun getAuthorizedEnvironments(): Collection<Environment> = Environment.entries

    var jobDb: remocra.db.jooq.remocra.tables.pojos.Job? = null
        get() {
            return jobRepository.getLatestExecution(getType())
        }

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    abstract fun checkParameters(parameters: T?)

    abstract fun getType(): TypeTask

    /**
     * Méthode permettant de savoir si ce type de tâche peut être exécuté dans l'absolu. Si elle ne l'est pas, rien à faire !
     * Marquée comme open pour pouvoir l'overrider en cas de besoin
     */
    open fun isActif(): Boolean {
        if (parametresProvider.get().mapTasksInfo[getType()] == null) {
            return false
        }
        return parametresProvider.get().mapTasksInfo[getType()]!!.taskActif!!
    }

    abstract fun getTaskParametersClass(): Class<T>

    fun start(logManager: LogManager, userInfo: UserInfo, parameters: T? = null): Boolean {
        // Si l'environnement n'est pas compatible avec la tâche, on ne fait rien
        if (!getAuthorizedEnvironments().contains(settings.environment)) {
            return false
        }

        this.logManager = logManager

        if (!isActif()) {
            return false
        }

        val taskParameters = parameters
            ?: objectMapper.readValue(parametresProvider.get().mapTasksInfo[getType()]!!.taskParametres!!.data(), getTaskParametersClass())

        // Insertion du job en base
        val result =
            transactionManager.transactionResult {
                jobRepository.createJob(
                    logManager.idJob,
                    parametresProvider.get().mapTasksInfo[getType()]!!.taskId,
                    userInfo.utilisateurId,
                    taskParameters?.let { JSONB.valueOf(objectMapper.writeValueAsString(it)) },
                )
            }
        if (result > 0) {
            logManager.info("Tâche lancée")
        }

        val latestJob = jobDb

        // Exécution du job à proprement parler
        job =
            launch {
                try {
                    checkParameters(taskParameters)
                    notify(taskParameters, execute(taskParameters, userInfo), logManager.idJob)
                    if (latestJob != null) {
                        transactionManager.transactionResult {
                            jobRepository.endJobSuccess(latestJob.jobId)
                        }
                    }
                    logManager.info("Tâche terminée")
                } catch (e: Exception) {
                    if (latestJob != null) {
                        transactionManager.transactionResult {
                            jobRepository.endJobError(latestJob.jobId)
                        }
                    }
                    logManager.error("Tâche terminée en erreur : ${e.message}")
                }
            }

        job.start()

        return job.isActive
    }

    /**
     * Méthode permettant de convertir un type abstrait de destinataire en liste concrète d'adresses mail à notifier lors de l'exécution d'un job. <br />
     * Chaque tâche devra s'adapter aux types concernés
     */
    abstract fun notifySpecific(executionResults: U?, notificationRaw: NotificationRaw)

    /**
     * Fonction permettant de gérer les notifications en fonction des paramètres de la tâche, appliqués aux paramètres du job. <br />
     * La tâche propose un comportement par défaut pour la notification, qui peut être overridé au besoin, d'où l'existence de la propriété parameters.notification ; si elle est vide, on utilise le comportement par défaut
     */
    open fun notify(parameters: T?, executionResults: U?, idJob: UUID) {
        // Si les paramètres de notification sont définis dans *parameters*, on est sur un override, il prime sur le reste
        if (parameters?.notification != null) {
            eventBus.post(NotificationEvent(parameters.notification!!, idJob))
        } else {
            val jsonNotif = parametresProvider.get().mapTasksInfo[getType()]!!.taskNotification?.data()
            try {
                if (jsonNotif == null) {
                    return
                }

                val notifications = objectMapper.readValue<NotificationRaw>(jsonNotif)
                notifySpecific(executionResults, notifications)
            } catch (jpe: JsonProcessingException) {
                logManager.error("Erreur lors de l'interprétation des paramètres de notification : ${jpe.message}")
            } catch (jme: JsonMappingException) {
                logManager.error("Erreur dans le format des paramètres de notification : ${jme.message}")
            }
        }
    }

    fun stop(): Boolean {
        if (!job.isActive) {
            return false
        }
        val latestJob = jobDb
        if (latestJob != null) {
            transactionManager.transactionResult {
                jobRepository.endJobError(latestJob.jobId)
            }
        }
        this.job.cancel()
        return job.isCancelled
    }

    fun getStatus(): Status {
        if (!this::job.isInitialized) {
            return Status(
                job = jobDb,
                isActive = false,
                isCompleted = false,
                isCancelled = false,
            )
        }
        return Status(
            job = jobDb,
            isActive = job.isActive,
            isCompleted = job.isCompleted,
            isCancelled = job.isCancelled,
        )
    }

    data class Status(
        val job: remocra.db.jooq.remocra.tables.pojos.Job?,
        val isActive: Boolean,
        val isCompleted: Boolean,
        val isCancelled: Boolean,
    )
}

/**
 * Résultats d'une tâche.
 */
open class JobResults()

/**
 * Paramètres d'une tâche.
 */
open class TaskParameters(
    // Le champ "notification" sera set uniquement si la task est lancée manuellement
    open val notification: NotificationMail?,
)

/**
 * Classe permettant de stocker les éléments de base pour déclencher une notification par mail au niveau d'un [Job].
 * Ces éléments sont <b>concrets</b>, il convient de les "calculer" par rapport au précâblage défini dans la tâche.
 */
data class NotificationMail(val destinataires: Set<String>, val objet: String, val corps: String) {
    override fun toString(): String {
        return "Destinataires: ${destinataires.joinToString()}, Objet : $objet, Corps : $corps"
    }
}

data class TypeDestinataire(
    val contactOrganisme: Set<String>,
    val contactGestionnaire: Boolean,
    val utilisateurOrganisme: Set<String>,
    val saisieLibre: Set<String>,
)

/**
 * Classe permattant de stocker dans une [remocra.db.jooq.remocra.tables.pojos.Task] les critères abstraits pour une future notification par mail.
 * Ces critères seront ensuite transformés en données concrètes lors de l'exécution d'un job.
 */
data class NotificationRaw(val typeDestinataire: TypeDestinataire, val objet: String, val corps: String)
