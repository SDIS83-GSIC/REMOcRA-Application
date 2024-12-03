package remocra.data

import org.jooq.JSONB
import remocra.db.jooq.remocra.enums.TypeTask
import java.util.UUID

data class TaskInputData(
    val taskId: UUID,
    val taskType: TypeTask,
    val taskActif: Boolean = false,
    val taskPlanification: String? = null,
    val taskExecManuelle: Boolean = false,
    val taskParametres: JSONB? = null,
    val taskNotification: JSONB? = null,
)
