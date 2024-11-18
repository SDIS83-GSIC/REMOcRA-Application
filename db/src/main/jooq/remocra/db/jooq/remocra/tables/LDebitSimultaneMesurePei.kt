/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables

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
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.keys.L_DEBIT_SIMULTANE_MESURE_PEI_PKEY
import remocra.db.jooq.remocra.keys.L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_DEBIT_SIMULTANE_MESURE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_PEI_ID_FKEY
import remocra.db.jooq.remocra.tables.DebitSimultaneMesure.DebitSimultaneMesurePath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
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
open class LDebitSimultaneMesurePei(
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
         * The reference instance of
         * <code>remocra.l_debit_simultane_mesure_pei</code>
         */
        val L_DEBIT_SIMULTANE_MESURE_PEI: LDebitSimultaneMesurePei = LDebitSimultaneMesurePei()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.l_debit_simultane_mesure_pei.debit_simultane_mesure_id</code>.
     */
    val DEBIT_SIMULTANE_MESURE_ID: TableField<Record, UUID?> = createField(DSL.name("debit_simultane_mesure_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.l_debit_simultane_mesure_pei.pei_id</code>.
     */
    val PEI_ID: TableField<Record, UUID?> = createField(DSL.name("pei_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.l_debit_simultane_mesure_pei</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.l_debit_simultane_mesure_pei</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.l_debit_simultane_mesure_pei</code> table
     * reference
     */
    constructor() : this(DSL.name("l_debit_simultane_mesure_pei"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, L_DEBIT_SIMULTANE_MESURE_PEI, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class LDebitSimultaneMesurePeiPath : LDebitSimultaneMesurePei, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): LDebitSimultaneMesurePeiPath = LDebitSimultaneMesurePeiPath(DSL.name(alias), this)
        override fun `as`(alias: Name): LDebitSimultaneMesurePeiPath = LDebitSimultaneMesurePeiPath(alias, this)
        override fun `as`(alias: Table<*>): LDebitSimultaneMesurePeiPath = LDebitSimultaneMesurePeiPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = L_DEBIT_SIMULTANE_MESURE_PEI_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_DEBIT_SIMULTANE_MESURE_ID_FKEY, L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_PEI_ID_FKEY)

    private lateinit var _debitSimultaneMesure: DebitSimultaneMesurePath

    /**
     * Get the implicit join path to the
     * <code>remocra.debit_simultane_mesure</code> table.
     */
    fun debitSimultaneMesure(): DebitSimultaneMesurePath {
        if (!this::_debitSimultaneMesure.isInitialized) {
            _debitSimultaneMesure = DebitSimultaneMesurePath(this, L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_DEBIT_SIMULTANE_MESURE_ID_FKEY, null)
        }

        return _debitSimultaneMesure
    }

    val debitSimultaneMesure: DebitSimultaneMesurePath
        get(): DebitSimultaneMesurePath = debitSimultaneMesure()

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit join path to the <code>remocra.pei</code> table.
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_PEI_ID_FKEY, null)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()
    override fun `as`(alias: String): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(DSL.name(alias), this)
    override fun `as`(alias: Name): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(alias, this)
    override fun `as`(alias: Table<*>): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): LDebitSimultaneMesurePei = LDebitSimultaneMesurePei(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): LDebitSimultaneMesurePei = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): LDebitSimultaneMesurePei = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): LDebitSimultaneMesurePei = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): LDebitSimultaneMesurePei = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): LDebitSimultaneMesurePei = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): LDebitSimultaneMesurePei = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): LDebitSimultaneMesurePei = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): LDebitSimultaneMesurePei = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): LDebitSimultaneMesurePei = where(DSL.notExists(select))
}
