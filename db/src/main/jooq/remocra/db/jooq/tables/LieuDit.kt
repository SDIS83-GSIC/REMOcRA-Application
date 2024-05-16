/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Geometry
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
import remocra.db.jooq.Remocra
import remocra.db.jooq.keys.LIEU_DIT_PKEY
import remocra.db.jooq.keys.LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY
import remocra.db.jooq.keys.PEI__PEI_PEI_LIEU_DIT_ID_FKEY
import remocra.db.jooq.tables.Commune.CommunePath
import remocra.db.jooq.tables.Pei.PeiPath
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
open class LieuDit(
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
         * The reference instance of <code>remocra.lieu_dit</code>
         */
        val LIEU_DIT: LieuDit = LieuDit()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.lieu_dit.lieu_dit_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("lieu_dit_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.lieu_dit.lieu_dit_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("lieu_dit_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.lieu_dit.lieu_dit_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("lieu_dit_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "")

    /**
     * The column <code>remocra.lieu_dit.lieu_dit_commune_id</code>.
     */
    val COMMUNE_ID: TableField<Record, UUID?> = createField(DSL.name("lieu_dit_commune_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.lieu_dit</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.lieu_dit</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.lieu_dit</code> table reference
     */
    constructor() : this(DSL.name("lieu_dit"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, LIEU_DIT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class LieuDitPath : LieuDit, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): LieuDitPath = LieuDitPath(DSL.name(alias), this)
        override fun `as`(alias: Name): LieuDitPath = LieuDitPath(alias, this)
        override fun `as`(alias: Table<*>): LieuDitPath = LieuDitPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = LIEU_DIT_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY)

    private lateinit var _commune: CommunePath

    /**
     * Get the implicit join path to the <code>remocra.commune</code> table.
     */
    fun commune(): CommunePath {
        if (!this::_commune.isInitialized) {
            _commune = CommunePath(this, LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY, null)
        }

        return _commune
    }

    val commune: CommunePath
        get(): CommunePath = commune()

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, null, PEI__PEI_PEI_LIEU_DIT_ID_FKEY.inverseKey)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()
    override fun `as`(alias: String): LieuDit = LieuDit(DSL.name(alias), this)
    override fun `as`(alias: Name): LieuDit = LieuDit(alias, this)
    override fun `as`(alias: Table<*>): LieuDit = LieuDit(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): LieuDit = LieuDit(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): LieuDit = LieuDit(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): LieuDit = LieuDit(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): LieuDit = LieuDit(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): LieuDit = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): LieuDit = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): LieuDit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): LieuDit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): LieuDit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): LieuDit = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): LieuDit = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): LieuDit = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): LieuDit = where(DSL.notExists(select))
}
