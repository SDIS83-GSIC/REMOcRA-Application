package remocra.data

import remocra.db.jooq.enums.TypeTask
import remocra.db.jooq.tables.pojos.Task

data class ParametresData(
    val mapParametres: Map<String, Any>,
    val mapTasksInfo: Map<TypeTask, Task>,
)
