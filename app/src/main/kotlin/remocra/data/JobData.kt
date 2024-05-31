package remocra.data

import remocra.db.jooq.tables.pojos.Job
import remocra.db.jooq.tables.pojos.LogLine

class JobData(
    val job: Job,
    val logLines: Collection<LogLine>?,
)
