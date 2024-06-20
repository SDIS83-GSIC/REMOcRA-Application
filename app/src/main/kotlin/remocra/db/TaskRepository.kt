package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Task
import remocra.db.jooq.remocra.tables.references.TASK

class TaskRepository @Inject constructor(private val dsl: DSLContext) {

    fun getMapTasks() = dsl.selectFrom(TASK).fetchInto<Task>().associateBy { it.taskType }
}
