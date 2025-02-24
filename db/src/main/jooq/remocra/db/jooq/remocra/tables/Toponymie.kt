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
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.bindings.GeometryBinding
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.keys.TOPONYMIE_PKEY
import remocra.db.jooq.remocra.keys.TOPONYMIE_TOPONYMIE_CODE_KEY
import remocra.db.jooq.remocra.keys.TOPONYMIE__TOPONYMIE_TYPE_TOPONYMIE_ID_FKEY
import remocra.db.jooq.remocra.tables.TypeToponymie.TypeToponymiePath
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
open class Toponymie(
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
         * The reference instance of <code>remocra.toponymie</code>
         */
        val TOPONYMIE: Toponymie = Toponymie()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.toponymie.toponymie_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("toponymie_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.toponymie.toponymie_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("toponymie_libelle"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.toponymie.toponymie_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("toponymie_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.toponymie.toponymie_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("toponymie_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "", GeometryBinding())

    /**
     * The column <code>remocra.toponymie.type_toponymie_id</code>.
     */
    val TYPE_TOPONYMIE_ID: TableField<Record, UUID?> = createField(DSL.name("type_toponymie_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.toponymie</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.toponymie</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.toponymie</code> table reference
     */
    constructor() : this(DSL.name("toponymie"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, TOPONYMIE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ToponymiePath : Toponymie, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ToponymiePath = ToponymiePath(DSL.name(alias), this)
        override fun `as`(alias: Name): ToponymiePath = ToponymiePath(alias, this)
        override fun `as`(alias: Table<*>): ToponymiePath = ToponymiePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = TOPONYMIE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(TOPONYMIE_TOPONYMIE_CODE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(TOPONYMIE__TOPONYMIE_TYPE_TOPONYMIE_ID_FKEY)

    private lateinit var _typeToponymie: TypeToponymiePath

    /**
     * Get the implicit join path to the <code>remocra.type_toponymie</code>
     * table.
     */
    fun typeToponymie(): TypeToponymiePath {
        if (!this::_typeToponymie.isInitialized) {
            _typeToponymie = TypeToponymiePath(this, TOPONYMIE__TOPONYMIE_TYPE_TOPONYMIE_ID_FKEY, null)
        }

        return _typeToponymie
    }

    val typeToponymie: TypeToponymiePath
        get(): TypeToponymiePath = typeToponymie()
    override fun `as`(alias: String): Toponymie = Toponymie(DSL.name(alias), this)
    override fun `as`(alias: Name): Toponymie = Toponymie(alias, this)
    override fun `as`(alias: Table<*>): Toponymie = Toponymie(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Toponymie = Toponymie(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Toponymie = Toponymie(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Toponymie = Toponymie(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Toponymie = Toponymie(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Toponymie = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Toponymie = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Toponymie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Toponymie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Toponymie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Toponymie = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Toponymie = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Toponymie = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Toponymie = where(DSL.notExists(select))
}
