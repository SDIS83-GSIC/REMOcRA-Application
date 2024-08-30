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
import remocra.db.jooq.remocra.keys.MATERIAU_MATERIAU_CODE_KEY
import remocra.db.jooq.remocra.keys.MATERIAU_PKEY
import remocra.db.jooq.remocra.keys.PENA__PENA_PENA_MATERIAU_ID_FKEY
import remocra.db.jooq.remocra.tables.Pena.PenaPath
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
open class Materiau(
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
         * The reference instance of <code>remocra.materiau</code>
         */
        val MATERIAU: Materiau = Materiau()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.materiau.materiau_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("materiau_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.materiau.materiau_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("materiau_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.materiau.materiau_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("materiau_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.materiau.materiau_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("materiau_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.materiau</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.materiau</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.materiau</code> table reference
     */
    constructor() : this(DSL.name("materiau"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, MATERIAU, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class MateriauPath : Materiau, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): MateriauPath = MateriauPath(DSL.name(alias), this)
        override fun `as`(alias: Name): MateriauPath = MateriauPath(alias, this)
        override fun `as`(alias: Table<*>): MateriauPath = MateriauPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = MATERIAU_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(MATERIAU_MATERIAU_CODE_KEY)

    private lateinit var _pena: PenaPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pena</code> table
     */
    fun pena(): PenaPath {
        if (!this::_pena.isInitialized) {
            _pena = PenaPath(this, null, PENA__PENA_PENA_MATERIAU_ID_FKEY.inverseKey)
        }

        return _pena
    }

    val pena: PenaPath
        get(): PenaPath = pena()
    override fun `as`(alias: String): Materiau = Materiau(DSL.name(alias), this)
    override fun `as`(alias: Name): Materiau = Materiau(alias, this)
    override fun `as`(alias: Table<*>): Materiau = Materiau(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Materiau = Materiau(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Materiau = Materiau(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Materiau = Materiau(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Materiau = Materiau(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Materiau = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Materiau = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Materiau = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Materiau = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Materiau = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Materiau = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Materiau = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Materiau = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Materiau = where(DSL.notExists(select))
}
