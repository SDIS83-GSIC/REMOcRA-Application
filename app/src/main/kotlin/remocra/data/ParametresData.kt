package remocra.data

import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.pojos.Task

data class ParametresData(
    val mapParametres: Map<String, Parametre>,
    val mapTasksInfo: Map<TypeTask, Task>,
)
