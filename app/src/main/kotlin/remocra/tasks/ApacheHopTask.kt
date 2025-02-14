package remocra.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.jooq.JSONB
import remocra.apachehop.ApacheHopApi
import remocra.db.TaskRepository
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Classe de base des tâches, correspondant à la définition d'un travail à effectuer sous forme de
 * [Job]
 */
class ApacheHopTask : CoroutineScope {
    @Inject
    private lateinit var taskRepository: TaskRepository

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Inject
    private lateinit var apacheHopApi: ApacheHopApi

    var schedulableTask: Job? = null

    fun getTaskApacheHop() = taskRepository.getTaskApacheHop()
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    fun execute(taskParametres: JSONB) {
        // On va chercher le nom de la task
        val apacheHopParametre = objectMapper.readValue(taskParametres.toString(), ApacheHopParametre::class.java)

        // Puis on demande à Apache Hop de l'exécuter
        apacheHopApi.run(apacheHopParametre.taskCode).execute()
    }

    /**
     * @property taskLibelle : le nom de la task
     * @property taskCode : le nom du workflow dans le fichier apache hop (par défaut le nom du fichier)
     */
    data class ApacheHopParametre(
        val taskLibelle: String,
        val taskCode: String,
    )
}
