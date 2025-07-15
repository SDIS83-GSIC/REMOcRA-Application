package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import remocra.data.JobData
import remocra.data.JobTask
import remocra.data.Params
import remocra.db.jooq.remocra.enums.EtatJob
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.Job.Companion.JOB
import remocra.db.jooq.remocra.tables.pojos.Job
import remocra.db.jooq.remocra.tables.pojos.LogLine
import remocra.db.jooq.remocra.tables.references.LOG_LINE
import remocra.db.jooq.remocra.tables.references.TASK
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

class JobRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

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

    fun listWithTask(
        params: Params<Filter, Sort>,
    ): List<JobTask> {
        val jobs = dsl.select(
            JOB.ID,
            TASK.TYPE.`as`("typeTask"),
            JOB.DATE_DEBUT,
            JOB.DATE_FIN,
            JOB.ETAT_JOB,
        )
            .from(JOB)
            .innerJoin(TASK).on(JOB.TASK_ID.eq(TASK.ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(JOB.DATE_DEBUT.desc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto<JobTask>()

        return jobs
    }

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
        val etatJob: EtatJob?,
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
                    etatJob?.let { JOB.ETAT_JOB.`in`(etatJob) },
                ),
            )
    }

    data class Sort(
        val typeTask: Int?,
        val jobDateDebut: Int?,
        val jobDateFin: Int?,
        val jobEtatJob: Int?,
    ) {
        fun toCondition() = listOfNotNull(
            TASK.TYPE.getSortField(typeTask),
            JOB.DATE_DEBUT.getSortField(jobDateDebut),
            JOB.DATE_FIN.getSortField(jobDateFin),
            JOB.ETAT_JOB.getSortField(jobEtatJob),
        )
    }

    fun getIdJobsOlderThanDays(nbDays: Long): List<UUID> =
        dsl.selectDistinct(JOB.ID)
            .from(JOB)
            .where(JOB.ETAT_JOB.`in`(EtatJob.TERMINE, EtatJob.NOTIFIE))
            .and(
                JOB.DATE_FIN
                    .plus(field("INTERVAL '$nbDays days'", String::class.java))
                    .lessThan(dateUtils.now()),
            )
            .fetchInto()

    fun purgeJobFromSetJobId(setJobId: Set<UUID>) =
        dsl.deleteFrom(JOB).where(JOB.ID.`in`(setJobId)).execute()
}
