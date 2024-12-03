package remocra.usecase.admin.task

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.jooq.JSONB
import remocra.db.TaskRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.tasks.NotificationRaw
import remocra.usecase.AbstractUseCase
import java.util.UUID

class TaskUseCase : AbstractUseCase() {

    @Inject lateinit var taskRepository: TaskRepository

    @Inject lateinit var objectMapper: ObjectMapper

    fun getTaskData(): List<TaskInfo> {
        val listMoulinette = taskRepository.getAllTaskInfoToAdmin()

        val objectForFront: MutableList<TaskInfo> = mutableListOf()
        listMoulinette.forEach { element ->
            objectForFront.add(
                TaskInfo(
                    taskId = element.taskId,
                    taskType = element.taskType,
                    taskActif = element.taskActif ?: false,
                    taskPlanification = element.taskPlanification,
                    taskExecManuelle = element.taskExecManuelle ?: false,
                    taskParametres = element.taskParametres,
                    taskNotification = element.taskNotification?.let { objectMapper.readValue(element.taskNotification!!.data(), NotificationRaw::class.java) },
                ),
            )
        }
        return objectForFront
    }
}

data class TaskInfo(
    val taskId: UUID,
    val taskType: TypeTask,
    val taskActif: Boolean,
    val taskPlanification: String?,
    val taskExecManuelle: Boolean,
    val taskParametres: JSONB?,
    val taskNotification: NotificationRaw?,
)
