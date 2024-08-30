/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.JSONB
import org.jooq.Name
import org.jooq.Path
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.enums.EtatJob
import remocra.db.jooq.remocra.keys.JOB_PKEY
import remocra.db.jooq.remocra.keys.JOB__JOB_JOB_TASK_ID_FKEY
import remocra.db.jooq.remocra.keys.LOG_LINE__LOG_LINE_LOG_LINE_JOB_ID_FKEY
import remocra.db.jooq.remocra.tables.LogLine.LogLinePath
import remocra.db.jooq.remocra.tables.Task.TaskPath
import java.time.ZonedDateTime
import java.util.UUID
import javax.annotation.processing.Generated
import kotlin.collections.Collection
import kotlin.collections.List

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
open class Job(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, Record>?,
    parentPath: InverseForeignKey<out Record, Record>?,
    aliased: Table<Record>?,
    parameters: Array<Field<*>?>?,
    where: Condition?,
) : TableImpl<Record>(
    alias,
    Remocra.REMOCRA,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>remocra.job</code>
         */
        val JOB: Job = Job()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.job.job_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("job_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.job.job_task_id</code>.
     */
    val TASK_ID: TableField<Record, UUID?> = createField(DSL.name("job_task_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.job.job_etat_job</code>.
     */
    val ETAT_JOB: TableField<Record, EtatJob?> = createField(DSL.name("job_etat_job"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(EtatJob::class.java), this, "")

    /**
     * The column <code>remocra.job.job_date_debut</code>.
     */
    val DATE_DEBUT: TableField<Record, ZonedDateTime?> = createField(DSL.name("job_date_debut"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "", ZonedDateTimeBinding())

    /**
     * The column <code>remocra.job.job_date_fin</code>.
     */
    val DATE_FIN: TableField<Record, ZonedDateTime?> = createField(DSL.name("job_date_fin"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "", ZonedDateTimeBinding())

    /**
     * The column <code>remocra.job.job_parametres</code>.
     */
    val PARAMETRES: TableField<Record, JSONB?> = createField(DSL.name("job_parametres"), SQLDataType.JSONB, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.job</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.job</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.job</code> table reference
     */
    constructor() : this(DSL.name("job"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, JOB, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class JobPath : Job, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): JobPath = JobPath(DSL.name(alias), this)
        override fun `as`(alias: Name): JobPath = JobPath(alias, this)
        override fun `as`(alias: Table<*>): JobPath = JobPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = JOB_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(JOB__JOB_JOB_TASK_ID_FKEY)

    private lateinit var _task: TaskPath

    /**
     * Get the implicit join path to the <code>remocra.task</code> table.
     */
    fun task(): TaskPath {
        if (!this::_task.isInitialized) {
            _task = TaskPath(this, JOB__JOB_JOB_TASK_ID_FKEY, null)
        }

        return _task
    }

    val task: TaskPath
        get(): TaskPath = task()

    private lateinit var _logLine: LogLinePath

    /**
     * Get the implicit to-many join path to the <code>remocra.log_line</code>
     * table
     */
    fun logLine(): LogLinePath {
        if (!this::_logLine.isInitialized) {
            _logLine = LogLinePath(this, null, LOG_LINE__LOG_LINE_LOG_LINE_JOB_ID_FKEY.inverseKey)
        }

        return _logLine
    }

    val logLine: LogLinePath
        get(): LogLinePath = logLine()
    override fun `as`(alias: String): Job = Job(DSL.name(alias), this)
    override fun `as`(alias: Name): Job = Job(alias, this)
    override fun `as`(alias: Table<*>): Job = Job(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Job = Job(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Job = Job(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Job = Job(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Job = Job(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Job = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Job = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Job = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Job = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Job = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Job = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Job = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Job = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Job = where(DSL.notExists(select))
}
