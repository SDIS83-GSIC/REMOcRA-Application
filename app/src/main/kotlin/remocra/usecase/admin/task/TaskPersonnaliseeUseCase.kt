package remocra.usecase.admin.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import org.jooq.JSONB
import remocra.db.TaskRepository
import remocra.tasks.ApacheHopTask
import remocra.usecase.AbstractUseCase
import java.util.UUID

class TaskPersonnaliseeUseCase : AbstractUseCase() {

    @Inject lateinit var taskRepository: TaskRepository

    @Inject lateinit var objectMapper: ObjectMapper

    fun getAllTaskPersonnaliseeData(): List<TaskPersonnaliseInfo> {
        val listMoulinette = taskRepository.getTaskApacheHopForAdmin()

        val objectForFront: MutableList<TaskPersonnaliseInfo> = mutableListOf()
        listMoulinette.forEach { element ->
            val param = element.taskParametres?.let { objectMapper.readValue<ApacheHopTask.ApacheHopParametre?>(it.toString()) }
            objectForFront.add(
                TaskPersonnaliseInfo(
                    taskId = element.taskId,
                    taskActif = element.taskActif ?: false,
                    taskPlanification = element.taskPlanification,
                    taskLibelle = param?.taskLibelle,
                    taskExecManuelle = false,
                    taskParametres = element.taskParametres,
                ),
            )
        }
        return objectForFront
    }
}

data class TaskPersonnaliseInfo(
    val taskId: UUID,
    val taskActif: Boolean,
    val taskPlanification: String?,
    val taskLibelle: String?,
    val taskExecManuelle: Boolean,
    val taskParametres: JSONB?,
)
