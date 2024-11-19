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
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.keys.DEBIT_SIMULTANE_MESURE_PKEY
import remocra.db.jooq.remocra.keys.DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_ID_FKEY
import remocra.db.jooq.remocra.keys.DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_MESURE_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_DEBIT_SIMULTANE_MESURE_ID_FKEY
import remocra.db.jooq.remocra.tables.DebitSimultane.DebitSimultanePath
import remocra.db.jooq.remocra.tables.Document.DocumentPath
import remocra.db.jooq.remocra.tables.LDebitSimultaneMesurePei.LDebitSimultaneMesurePeiPath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
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
open class DebitSimultaneMesure(
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
         * The reference instance of <code>remocra.debit_simultane_mesure</code>
         */
        val DEBIT_SIMULTANE_MESURE: DebitSimultaneMesure = DebitSimultaneMesure()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("debit_simultane_mesure_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_id</code>.
     */
    val DEBIT_SIMULTANE_ID: TableField<Record, UUID?> = createField(DSL.name("debit_simultane_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_debit_requis</code>.
     */
    val DEBIT_REQUIS: TableField<Record, Int?> = createField(DSL.name("debit_simultane_mesure_debit_requis"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_debit_mesure</code>.
     */
    val DEBIT_MESURE: TableField<Record, Int?> = createField(DSL.name("debit_simultane_mesure_debit_mesure"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_debit_retenu</code>.
     */
    val DEBIT_RETENU: TableField<Record, Int?> = createField(DSL.name("debit_simultane_mesure_debit_retenu"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_date_mesure</code>.
     */
    val DATE_MESURE: TableField<Record, ZonedDateTime?> = createField(DSL.name("debit_simultane_mesure_date_mesure"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_commentaire</code>.
     */
    val COMMENTAIRE: TableField<Record, String?> = createField(DSL.name("debit_simultane_mesure_commentaire"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_identique_reseau_ville</code>.
     */
    val IDENTIQUE_RESEAU_VILLE: TableField<Record, Boolean?> = createField(DSL.name("debit_simultane_mesure_identique_reseau_ville"), SQLDataType.BOOLEAN, this, "")

    /**
     * The column
     * <code>remocra.debit_simultane_mesure.debit_simultane_mesure_document_id</code>.
     */
    val DOCUMENT_ID: TableField<Record, UUID?> = createField(DSL.name("debit_simultane_mesure_document_id"), SQLDataType.UUID, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.debit_simultane_mesure</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.debit_simultane_mesure</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.debit_simultane_mesure</code> table reference
     */
    constructor() : this(DSL.name("debit_simultane_mesure"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, DEBIT_SIMULTANE_MESURE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class DebitSimultaneMesurePath : DebitSimultaneMesure, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): DebitSimultaneMesurePath = DebitSimultaneMesurePath(DSL.name(alias), this)
        override fun `as`(alias: Name): DebitSimultaneMesurePath = DebitSimultaneMesurePath(alias, this)
        override fun `as`(alias: Table<*>): DebitSimultaneMesurePath = DebitSimultaneMesurePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = DEBIT_SIMULTANE_MESURE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_ID_FKEY, DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_MESURE_DOCUMENT_ID_FKEY)

    private lateinit var _debitSimultane: DebitSimultanePath

    /**
     * Get the implicit join path to the <code>remocra.debit_simultane</code>
     * table.
     */
    fun debitSimultane(): DebitSimultanePath {
        if (!this::_debitSimultane.isInitialized) {
            _debitSimultane = DebitSimultanePath(this, DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_ID_FKEY, null)
        }

        return _debitSimultane
    }

    val debitSimultane: DebitSimultanePath
        get(): DebitSimultanePath = debitSimultane()

    private lateinit var _document: DocumentPath

    /**
     * Get the implicit join path to the <code>remocra.document</code> table.
     */
    fun document(): DocumentPath {
        if (!this::_document.isInitialized) {
            _document = DocumentPath(this, DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_MESURE_DOCUMENT_ID_FKEY, null)
        }

        return _document
    }

    val document: DocumentPath
        get(): DocumentPath = document()

    private lateinit var _lDebitSimultaneMesurePei: LDebitSimultaneMesurePeiPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_debit_simultane_mesure_pei</code> table
     */
    fun lDebitSimultaneMesurePei(): LDebitSimultaneMesurePeiPath {
        if (!this::_lDebitSimultaneMesurePei.isInitialized) {
            _lDebitSimultaneMesurePei = LDebitSimultaneMesurePeiPath(this, null, L_DEBIT_SIMULTANE_MESURE_PEI__L_DEBIT_SIMULTANE_MESURE_PEI_DEBIT_SIMULTANE_MESURE_ID_FKEY.inverseKey)
        }

        return _lDebitSimultaneMesurePei
    }

    val lDebitSimultaneMesurePei: LDebitSimultaneMesurePeiPath
        get(): LDebitSimultaneMesurePeiPath = lDebitSimultaneMesurePei()

    /**
     * Get the implicit many-to-many join path to the <code>remocra.pei</code>
     * table
     */
    val pei: PeiPath
        get(): PeiPath = lDebitSimultaneMesurePei().pei()
    override fun `as`(alias: String): DebitSimultaneMesure = DebitSimultaneMesure(DSL.name(alias), this)
    override fun `as`(alias: Name): DebitSimultaneMesure = DebitSimultaneMesure(alias, this)
    override fun `as`(alias: Table<*>): DebitSimultaneMesure = DebitSimultaneMesure(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): DebitSimultaneMesure = DebitSimultaneMesure(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): DebitSimultaneMesure = DebitSimultaneMesure(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): DebitSimultaneMesure = DebitSimultaneMesure(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): DebitSimultaneMesure = DebitSimultaneMesure(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): DebitSimultaneMesure = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): DebitSimultaneMesure = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): DebitSimultaneMesure = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): DebitSimultaneMesure = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): DebitSimultaneMesure = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): DebitSimultaneMesure = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): DebitSimultaneMesure = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): DebitSimultaneMesure = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): DebitSimultaneMesure = where(DSL.notExists(select))
}
