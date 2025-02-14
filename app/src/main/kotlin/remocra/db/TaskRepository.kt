package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.db.jooq.remocra.tables.references.TASK

class TaskRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getMapTasks() = dsl.selectFrom(TASK).fetchInto<Task>().associateBy { it.taskType }

    fun getAllTaskInfoToAdmin(): List<Task> = dsl.selectFrom(TASK).fetchInto()

    fun update(task: Task): Int =
        dsl.update(TASK)
            .set(TASK.ACTIF, task.taskActif)
            .set(TASK.PLANIFICATION, task.taskPlanification)
            .set(TASK.EXEC_MANUELLE, task.taskExecManuelle)
            .set(TASK.PARAMETRES, task.taskParametres)
            .set(TASK.NOTIFICATION, task.taskNotification)
            .where(TASK.ID.eq(task.taskId))
            .execute()

    fun getTaskApacheHop(): Collection<Task> =
        dsl.selectFrom(TASK).where(TASK.TYPE.eq(TypeTask.PERSONNALISE)).and(TASK.ACTIF.eq(true)).fetchInto()
}
