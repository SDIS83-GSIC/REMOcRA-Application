package remocra.data

import remocra.db.jooq.remocra.enums.EtatJob
import remocra.db.jooq.remocra.enums.TypeTask
import java.time.ZonedDateTime
import java.util.UUID

data class JobTask(
    val jobId: UUID,
    val taskType: TypeTask,
    var jobDateDebut: ZonedDateTime,
    val jobDateFin: ZonedDateTime?,
    val jobEtatJob: EtatJob,
)
