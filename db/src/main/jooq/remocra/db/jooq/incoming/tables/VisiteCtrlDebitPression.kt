/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.incoming.tables

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
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
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import remocra.db.jooq.incoming.Incoming
import remocra.db.jooq.incoming.keys.VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY
import remocra.db.jooq.incoming.tables.Visite.VisitePath
import java.math.BigDecimal
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
open class VisiteCtrlDebitPression(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, Record>?,
    parentPath: InverseForeignKey<out Record, Record>?,
    aliased: Table<Record>?,
    parameters: Array<Field<*>?>?,
    where: Condition?,
) : TableImpl<Record>(
    alias,
    Incoming.INCOMING,
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
         * The reference instance of
         * <code>incoming.visite_ctrl_debit_pression</code>
         */
        val VISITE_CTRL_DEBIT_PRESSION: VisiteCtrlDebitPression = VisiteCtrlDebitPression()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>incoming.visite_ctrl_debit_pression.visite_ctrl_debit_pression_visite_id</code>.
     */
    val VISITE_ID: TableField<Record, UUID?> = createField(DSL.name("visite_ctrl_debit_pression_visite_id"), SQLDataType.UUID, this, "")

    /**
     * The column
     * <code>incoming.visite_ctrl_debit_pression.visite_ctrl_debit_pression_debit</code>.
     */
    val DEBIT: TableField<Record, Int?> = createField(DSL.name("visite_ctrl_debit_pression_debit"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>incoming.visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression</code>.
     */
    val PRESSION: TableField<Record, BigDecimal?> = createField(DSL.name("visite_ctrl_debit_pression_pression"), SQLDataType.NUMERIC(5, 2), this, "")

    /**
     * The column
     * <code>incoming.visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression_dyn</code>.
     */
    val PRESSION_DYN: TableField<Record, BigDecimal?> = createField(DSL.name("visite_ctrl_debit_pression_pression_dyn"), SQLDataType.NUMERIC(5, 2), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>incoming.visite_ctrl_debit_pression</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>incoming.visite_ctrl_debit_pression</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>incoming.visite_ctrl_debit_pression</code> table reference
     */
    constructor() : this(DSL.name("visite_ctrl_debit_pression"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, VISITE_CTRL_DEBIT_PRESSION, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class VisiteCtrlDebitPressionPath : VisiteCtrlDebitPression, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): VisiteCtrlDebitPressionPath = VisiteCtrlDebitPressionPath(DSL.name(alias), this)
        override fun `as`(alias: Name): VisiteCtrlDebitPressionPath = VisiteCtrlDebitPressionPath(alias, this)
        override fun `as`(alias: Table<*>): VisiteCtrlDebitPressionPath = VisiteCtrlDebitPressionPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Incoming.INCOMING
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY)

    private lateinit var _visite: VisitePath

    /**
     * Get the implicit join path to the <code>incoming.visite</code> table.
     */
    fun visite(): VisitePath {
        if (!this::_visite.isInitialized) {
            _visite = VisitePath(this, VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY, null)
        }

        return _visite
    }

    val visite: VisitePath
        get(): VisitePath = visite()
    override fun `as`(alias: String): VisiteCtrlDebitPression = VisiteCtrlDebitPression(DSL.name(alias), this)
    override fun `as`(alias: Name): VisiteCtrlDebitPression = VisiteCtrlDebitPression(alias, this)
    override fun `as`(alias: Table<*>): VisiteCtrlDebitPression = VisiteCtrlDebitPression(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): VisiteCtrlDebitPression = VisiteCtrlDebitPression(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): VisiteCtrlDebitPression = VisiteCtrlDebitPression(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): VisiteCtrlDebitPression = VisiteCtrlDebitPression(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): VisiteCtrlDebitPression = VisiteCtrlDebitPression(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): VisiteCtrlDebitPression = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): VisiteCtrlDebitPression = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): VisiteCtrlDebitPression = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): VisiteCtrlDebitPression = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): VisiteCtrlDebitPression = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): VisiteCtrlDebitPression = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): VisiteCtrlDebitPression = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): VisiteCtrlDebitPression = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): VisiteCtrlDebitPression = where(DSL.notExists(select))
}
