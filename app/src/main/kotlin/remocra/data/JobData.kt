package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Job
import remocra.db.jooq.remocra.tables.pojos.LogLine

class JobData(
    val job: Job,
    val logLines: Collection<LogLine>?,
)
