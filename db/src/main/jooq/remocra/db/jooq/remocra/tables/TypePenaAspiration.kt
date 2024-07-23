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
import remocra.db.jooq.remocra.keys.PENA_ASPIRATION__PENA_ASPIRATION_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_ID_FKEY
import remocra.db.jooq.remocra.keys.TYPE_PENA_ASPIRATION_PKEY
import remocra.db.jooq.remocra.keys.TYPE_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_CODE_KEY
import remocra.db.jooq.remocra.tables.PenaAspiration.PenaAspirationPath
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
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
open class TypePenaAspiration(
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
         * The reference instance of <code>remocra.type_pena_aspiration</code>
         */
        val TYPE_PENA_ASPIRATION: TypePenaAspiration = TypePenaAspiration()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.type_pena_aspiration.type_pena_aspiration_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("type_pena_aspiration_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_pena_aspiration.type_pena_aspiration_type_actif</code>.
     */
    val TYPE_ACTIF: TableField<Record, Boolean?> = createField(DSL.name("type_pena_aspiration_type_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_pena_aspiration.type_pena_aspiration_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("type_pena_aspiration_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_pena_aspiration.type_pena_aspiration_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("type_pena_aspiration_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.type_pena_aspiration</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.type_pena_aspiration</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.type_pena_aspiration</code> table reference
     */
    constructor() : this(DSL.name("type_pena_aspiration"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, TYPE_PENA_ASPIRATION, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class TypePenaAspirationPath : TypePenaAspiration, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): TypePenaAspirationPath = TypePenaAspirationPath(DSL.name(alias), this)
        override fun `as`(alias: Name): TypePenaAspirationPath = TypePenaAspirationPath(alias, this)
        override fun `as`(alias: Table<*>): TypePenaAspirationPath = TypePenaAspirationPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = TYPE_PENA_ASPIRATION_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(TYPE_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_CODE_KEY)

    private lateinit var _penaAspiration: PenaAspirationPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.pena_aspiration</code> table
     */
    fun penaAspiration(): PenaAspirationPath {
        if (!this::_penaAspiration.isInitialized) {
            _penaAspiration = PenaAspirationPath(this, null, PENA_ASPIRATION__PENA_ASPIRATION_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_ID_FKEY.inverseKey)
        }

        return _penaAspiration
    }

    val penaAspiration: PenaAspirationPath
        get(): PenaAspirationPath = penaAspiration()
    override fun `as`(alias: String): TypePenaAspiration = TypePenaAspiration(DSL.name(alias), this)
    override fun `as`(alias: Name): TypePenaAspiration = TypePenaAspiration(alias, this)
    override fun `as`(alias: Table<*>): TypePenaAspiration = TypePenaAspiration(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): TypePenaAspiration = TypePenaAspiration(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): TypePenaAspiration = TypePenaAspiration(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): TypePenaAspiration = TypePenaAspiration(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): TypePenaAspiration = TypePenaAspiration(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): TypePenaAspiration = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): TypePenaAspiration = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): TypePenaAspiration = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): TypePenaAspiration = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): TypePenaAspiration = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): TypePenaAspiration = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): TypePenaAspiration = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): TypePenaAspiration = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): TypePenaAspiration = where(DSL.notExists(select))
}
