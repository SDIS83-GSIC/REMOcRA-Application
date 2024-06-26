package remocra.tasks

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jooq.JSONB
import remocra.app.AppSettings
import remocra.data.ParametresData
import remocra.data.enums.Environment
import remocra.db.JobRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.eventbus.EventBus
import remocra.eventbus.notification.NotificationEvent
import remocra.log.LogManager
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Classe de base des tâches, correspondant à la définition d'un travail à effectuer sous forme de
 * [Job]
 */
abstract class SimpleTask<T : TaskParameters> : CoroutineScope {

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
    lateinit var parametresData: ParametresData

    @Inject
    lateinit var eventBus: EventBus

    /**
     * Méthode exécutant le code de la tâche à proprement parler.
     * Doit être fait de manière bloquante, pas d'appels asynchrones, car la notification est faite dans la foulée
     */
    protected abstract fun execute(parameters: T?)

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
        if (parametresData.mapTasksInfo[getType()] == null) {
            return false
        }
        return parametresData.mapTasksInfo[getType()]!!.taskActif!!
    }

    abstract fun getTaskParametersClass(): Class<T>

    fun start(logManager: LogManager, parameters: T? = null): Boolean {
        // Si l'environnement n'est pas compatible avec la tâche, on ne fait rien
        if (!getAuthorizedEnvironments().contains(settings.environment)) {
            return false
        }

        this.logManager = logManager

        if (!isActif()) {
            return false
        }

        val taskParameters = parameters
            ?: objectMapper.readValue(parametresData.mapTasksInfo[getType()]!!.taskParametres!!.data(), getTaskParametersClass())

        // Insertion du job en base
        val result =
            transactionManager.transactionResult {
                jobRepository.createJob(
                    logManager.idJob,
                    parametresData.mapTasksInfo[getType()]!!.taskId,
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
                    execute(taskParameters)
                    notify(taskParameters, logManager.idJob)
                } catch (e: Exception) {
                    if (latestJob != null) {
                        transactionManager.transaction {
                            jobRepository.endJobError(latestJob.jobId)
                        }
                    }
                    logManager.error("Tâche terminée en erreur : ${e.message}")
                } finally {
                    if (latestJob != null) {
                        transactionManager.transaction {
                            jobRepository.endJobSuccess(latestJob.jobId)
                        }
                    }
                    logManager.info("Tâche terminée")
                }
            }

        job.start()

        return job.isActive
    }

    /**
     * Fonction permettant de gérer les notifications en fonction des paramètres de la tâche, appliqués aux paramètres du job. <br />
     * La tâche propose un comportement par défaut pour la notification, qui peut être overridé au besoin, d'où l'existence de la propriété parameters.notification ; si elle est vide, on utilise le comportement par défaut
     */
    open fun notify(parameters: T?, idJob: UUID) {
        // Si les paramètres de notification sont définis dans *parameters*, on est sur un override, il prime sur le reste
        if (parameters?.notification != null) {
            eventBus.post(NotificationEvent(parameters.notification!!, idJob))
        } else {
            val jsonNotif = parametresData.mapTasksInfo[getType()]!!.taskNotification?.data()
            try {
                if (jsonNotif == null) {
                    return
                }

                val notifications = objectMapper.readValue<NotificationRaw>(jsonNotif)
                notifications.typeDestinataire.forEach {
                    eventBus.post(NotificationEvent(NotificationMail(destinataires = getDestinatairesFromType(it), objet = notifications.objet, corps = notifications.corps), idJob))
                }
            } catch (jpe: JsonProcessingException) {
                logManager.error("Erreur lors de l'interprétation des paramètres de notification : ${jpe.message}")
            } catch (jme: JsonMappingException) {
                logManager.error("Erreur dans le format des paramètres de notification : ${jme.message}")
            }
        }
    }

    /**
     * Méthode permettant de convertir un type abstrait de destinataire en liste concrète d'adresses mail à notifier lors de l'exécution d'un job. <br />
     * Chaque tâche devra s'adapter aux types concernés
     */
    abstract fun getDestinatairesFromType(typeDestinataire: String): Set<String>

    fun stop(): Boolean {
        if (!job.isActive) {
            return false
        }
        val latestJob = jobDb
        if (latestJob != null) {
            transactionManager.transaction {
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

/**
 * Classe permattant de stocker dans une [remocra.db.jooq.remocra.tables.pojos.Task] les critères abstraits pour une future notification par mail.
 * Ces critères seront ensuite transformés en données concrètes lors de l'exécution d'un job.
 */
data class NotificationRaw(val typeDestinataire: Set<String>, val objet: String, val corps: String)
