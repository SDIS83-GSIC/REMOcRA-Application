package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.DSL
import remocra.data.JobData
import remocra.db.jooq.remocra.enums.EtatJob
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.Job.Companion.JOB
import remocra.db.jooq.remocra.tables.pojos.Job
import remocra.db.jooq.remocra.tables.pojos.LogLine
import remocra.db.jooq.remocra.tables.references.LOG_LINE
import remocra.db.jooq.remocra.tables.references.TASK
import remocra.utils.DateUtils
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

class JobRepository @Inject constructor(private val dsl: DSLContext, private val dateUtils: DateUtils) {

    fun createJob(idJob: UUID, idTask: UUID, userId: UUID, parameters: JSONB? = null): Int =
        dsl.insertInto(JOB)
            .set(
                dsl.newRecord(
                    JOB,
                    Job(
                        idJob,
                        idTask,
                        EtatJob.EN_COURS,
                        dateUtils.now(),
                        null,
                        parameters,
                        userId,
                    ),
                ),
            )
            .onConflictDoNothing()
            .execute()

    fun getLatestExecution(taskType: TypeTask): Job? =
        dsl.select(*JOB.fields()).from(JOB)
            .innerJoin(TASK).on(JOB.TASK_ID.eq(TASK.ID))
            .where(TASK.TYPE.eq(taskType))
            .orderBy(JOB.DATE_DEBUT.desc(), JOB.DATE_FIN.desc().nullsFirst())
            .limit(1)
            .fetchOneInto()

    fun getPreviousExecution(taskType: TypeTask, jobId: UUID): Job? =
        dsl.select(*JOB.fields()).from(JOB)
            .innerJoin(TASK).on(JOB.TASK_ID.eq(TASK.ID))
            .where(TASK.TYPE.eq(taskType))
            .and(JOB.ETAT_JOB.`in`(EtatJob.NOTIFIE, EtatJob.TERMINE))
            .and(JOB.ID.ne(jobId))
            .orderBy(JOB.DATE_DEBUT.desc(), JOB.DATE_FIN.desc().nullsFirst())
            .limit(1)
            .fetchOneInto()

    fun endJobSuccess(idJob: UUID): Job =
        dsl.update(JOB)
            .set(JOB.ETAT_JOB, EtatJob.TERMINE)
            .set(JOB.DATE_FIN, dateUtils.now())
            .where(JOB.ID.eq(idJob))
            .returning()
            .fetchSingleInto()

    fun endJobError(idJob: UUID): Job =
        dsl.update(JOB)
            .set(JOB.ETAT_JOB, EtatJob.EN_ERREUR)
            .set(JOB.DATE_FIN, dateUtils.now())
            .where(JOB.ID.eq(idJob))
            .returning()
            .fetchSingleInto()

    fun setNotifie(idJob: UUID): Job =
        dsl.update(JOB)
            .set(JOB.ETAT_JOB, EtatJob.NOTIFIE)
            .where(JOB.ID.eq(idJob))
            .returning()
            .fetchSingleInto()

    fun list(limit: Int?, offset: Int?, filter: Filter?): List<Job> =
        dsl.select(*JOB.fields())
            .from(JOB)
            .innerJoin(TASK).on(JOB.TASK_ID.eq(TASK.ID))
            .where(filter?.toCondition())
            .orderBy(JOB.DATE_DEBUT.desc())
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun countJobs(filter: Filter?): Int =
        dsl.selectCount().from(JOB).innerJoin(TASK).on(JOB.TASK_ID.eq(TASK.ID)).where(filter?.toCondition()).fetchSingleInto()

    fun getJobWithLogLines(idJob: UUID): JobData {
        val job: Job = dsl.selectFrom(JOB).where(JOB.ID.eq(idJob)).fetchSingleInto()
        val logLines: Collection<LogLine> =
            dsl.selectFrom(LOG_LINE).where(LOG_LINE.JOB_ID.eq(idJob)).fetchInto()
        return JobData(job, logLines)
    }

    data class Filter(
        val typeTask: TypeTask?,
        val dateDebutJob: LocalDate?,
        val dateFinJob: LocalDate?,
        val etatsJob: Collection<EtatJob>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    typeTask?.let { TASK.TYPE.eq(typeTask) },
                    dateDebutJob?.let {
                        JOB.DATE_DEBUT.ge(ZonedDateTime.of(it.atStartOfDay(), ZoneOffset.UTC))
                    },
                    dateFinJob?.let {
                        JOB.DATE_FIN.lt(
                            ZonedDateTime.of(it.atStartOfDay().plusDays(1), ZoneOffset.UTC),
                        )
                    },
                    etatsJob?.let { JOB.ETAT_JOB.`in`(etatsJob) },
                ),
            )
    }
}
