package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.tables.pojos.LogLine
import remocra.db.jooq.tables.references.LOG_LINE
import java.util.UUID

class LogLineRepository @Inject constructor(private val dsl: DSLContext) {

    fun writeLogLine(logLine: LogLine): Int {
        return dsl.insertInto(LOG_LINE).set(dsl.newRecord(LOG_LINE, logLine)).execute()
    }

    fun getLogLines(idJob: UUID): Collection<LogLine> {
        return dsl.selectFrom(LOG_LINE).where(LOG_LINE.JOB_ID.eq(idJob)).fetchInto()
    }

    fun getLogLinesWithIdObject(idObject: UUID): Collection<LogLine> {
        return dsl.selectFrom(LOG_LINE).where(LOG_LINE.OBJECT_ID.eq(idObject)).fetchInto()
    }
}
