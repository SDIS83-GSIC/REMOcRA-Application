package remocra.data

import org.jooq.JSONB
import remocra.db.jooq.remocra.enums.TypeTask
import java.io.InputStream
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

data class TaskPersonnaliseeInputData(
    val taskId: UUID,
    val taskActif: Boolean = false,
    val taskPlanification: String? = null,
    val taskParametres: JSONB? = null,
    val zip: InputStream? = null,
)
