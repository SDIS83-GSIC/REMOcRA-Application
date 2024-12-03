package remocra.web.parametres

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.jooq.JSONB
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.TaskInputData
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.usecase.admin.task.TaskUseCase
import remocra.usecase.admin.task.UpdateTaskUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
class TaskEndpoint : AbstractEndpoint() {
    @Inject lateinit var taskUseCase: TaskUseCase

    @Inject lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var objectMapper: ObjectMapper

    @Path("/")
    @GET
    @RequireDroits([Droit.ADMIN_PARAM_TRAITEMENTS])
    fun getTasks(): Response =
        Response.ok(
            taskUseCase.getTaskData(),
        ).build()

    @Path("/")
    @PUT
    @RequireDroits([Droit.ADMIN_PARAM_TRAITEMENTS])
    fun updateTask(taskInput: TaskInput): Response =
        updateTaskUseCase.execute(
            securityContext.userInfo,
            TaskInputData(
                taskId = taskInput.taskId,
                taskType = taskInput.taskType,
                taskActif = taskInput.taskActif,
                taskPlanification = taskInput.taskPlanification,
                taskExecManuelle = taskInput.taskExecManuelle,
                taskParametres = taskInput.taskParametres,
                taskNotification = taskInput.taskNotification,
            ),
        ).wrap()

    class TaskInput {
        @FormParam("taskId")
        lateinit var taskId: UUID

        @FormParam("taskType")
        lateinit var taskType: TypeTask

        @FormParam("taskActif")
        val taskActif: Boolean = false

        @FormParam("taskPlanification")
        val taskPlanification: String? = null

        @FormParam("taskExecManuelle")
        val taskExecManuelle: Boolean = false

        @FormParam("taskParametres")
        val taskParametres: JSONB? = null

        @FormParam("taskNotification")
        val taskNotification: JSONB? = null
    }
}
