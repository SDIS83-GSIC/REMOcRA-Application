package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.enums.LogLineGravity
import remocra.db.jooq.remocra.tables.pojos.LogLine
import remocra.db.jooq.remocra.tables.references.LOG_LINE
import java.util.UUID

class LogLineRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun writeLogLine(logLine: LogLine): Int {
        return dsl.insertInto(LOG_LINE).set(dsl.newRecord(LOG_LINE, logLine)).execute()
    }

    fun getLogLines(idJob: UUID, params: Params<Filter, Sort>): Collection<LogLine> {
        return dsl.selectFrom(LOG_LINE)
            .where(LOG_LINE.JOB_ID.eq(idJob))
            .and(LOG_LINE.JOB_ID.eq(idJob))
            .and(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(LOG_LINE.DATE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()
    }

    fun countLogLines(idJob: UUID, filter: Filter?): Int =
        dsl.selectCount()
            .from(LOG_LINE)
            .where(LOG_LINE.JOB_ID.eq(idJob))
            .and(filter?.toCondition()).fetchSingleInto()

    fun getLogLinesWithIdObject(idObject: UUID): Collection<LogLine> {
        return dsl.selectFrom(LOG_LINE).where(LOG_LINE.OBJECT_ID.eq(idObject)).fetchInto()
    }

    fun purgeLogLineFromListJobId(setJobId: Set<UUID>) =
        dsl.deleteFrom(LOG_LINE).where(LOG_LINE.JOB_ID.`in`(setJobId)).execute()

    data class Filter(val logLineGravity: LogLineGravity?) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    logLineGravity?.let { LOG_LINE.GRAVITY.eq(logLineGravity) },
                ),
            )
    }

    data class Sort(val logLineGravity: Int?, val logLineDate: Int?) {
        fun toCondition() = listOfNotNull(
            LOG_LINE.GRAVITY.getSortField(logLineGravity),
            LOG_LINE.DATE.getSortField(logLineDate),
        )
    }
}
