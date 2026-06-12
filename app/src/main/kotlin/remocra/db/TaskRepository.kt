package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.db.jooq.remocra.tables.references.TASK
import java.util.UUID

class TaskRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getMapTasks() = dsl.selectFrom(TASK).fetchInto<Task>().associateBy { it.taskType }

    fun getAllTaskInfoToAdmin(): List<Task> = dsl.selectFrom(TASK).fetchInto()
    fun getAllTaskApacheHop(taskId: UUID? = null): List<Task> =
        dsl.selectFrom(TASK)
            .where(TASK.TYPE.eq(TypeTask.PERSONNALISE))
            .and(taskId?.let { TASK.ID.ne(it) } ?: DSL.noCondition()).fetchInto()

    fun update(task: Task): Int =
        dsl.update(TASK)
            .set(TASK.ACTIF, task.taskActif)
            .set(TASK.PLANIFICATION, task.taskPlanification)
            .set(TASK.EXEC_MANUELLE, task.taskExecManuelle)
            .set(TASK.PARAMETRES, task.taskParametres)
            .set(TASK.NOTIFICATION, task.taskNotification)
            .where(TASK.ID.eq(task.taskId))
            .execute()

    fun insert(task: Task): Int =
        dsl.insertInto(TASK)
            .set(TASK.ID, task.taskId)
            .set(TASK.ACTIF, task.taskActif)
            .set(TASK.TYPE, task.taskType)
            .set(TASK.PLANIFICATION, task.taskPlanification)
            .set(TASK.EXEC_MANUELLE, task.taskExecManuelle)
            .set(TASK.PARAMETRES, task.taskParametres)
            .set(TASK.NOTIFICATION, task.taskNotification)
            .execute()

    fun getTaskApacheHopForAdmin(): Collection<Task> =
        dsl.selectFrom(TASK).where(TASK.TYPE.eq(TypeTask.PERSONNALISE)).fetchInto()

    fun getTaskApacheHop(): Collection<Task> =
        dsl.selectFrom(TASK).where(TASK.TYPE.eq(TypeTask.PERSONNALISE)).and(TASK.ACTIF.eq(true)).fetchInto()

    fun deleteTaskPersonnalisee(taskId: UUID) =
        dsl.deleteFrom(TASK).where(TASK.ID.eq(taskId).and(TASK.TYPE.eq(TypeTask.PERSONNALISE))).execute()

    fun getTaskById(taskId: UUID): Task? =
        dsl.selectFrom(TASK).where(TASK.ID.eq(taskId)).fetchOneInto()
}
