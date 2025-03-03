/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables

import org.jooq.Check
import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
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
import remocra.db.jooq.remocra.indexes.SITE_GEOMETRIE_IDX
import remocra.db.jooq.remocra.keys.DEBIT_SIMULTANE__DEBIT_SIMULTANE_DEBIT_SIMULTANE_SITE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_CONTACT_GESTIONNAIRE__L_CONTACT_GESTIONNAIRE_SITE_ID_FKEY
import remocra.db.jooq.remocra.keys.PEI__PEI_PEI_SITE_ID_FKEY
import remocra.db.jooq.remocra.keys.SITE_PKEY
import remocra.db.jooq.remocra.keys.SITE_SITE_CODE_KEY
import remocra.db.jooq.remocra.keys.SITE__SITE_SITE_GESTIONNAIRE_ID_FKEY
import remocra.db.jooq.remocra.tables.DebitSimultane.DebitSimultanePath
import remocra.db.jooq.remocra.tables.Gestionnaire.GestionnairePath
import remocra.db.jooq.remocra.tables.LContactGestionnaire.LContactGestionnairePath
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
open class Site(
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
         * The reference instance of <code>remocra.site</code>
         */
        val SITE: Site = Site()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.site.site_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("site_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.site.site_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("site_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.site.site_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("site_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.site.site_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("site_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.site.site_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("site_geometrie"), SQLDataType.GEOMETRY, this, "", GeometryBinding())

    /**
     * The column <code>remocra.site.site_gestionnaire_id</code>.
     */
    val GESTIONNAIRE_ID: TableField<Record, UUID?> = createField(DSL.name("site_gestionnaire_id"), SQLDataType.UUID, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.site</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.site</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.site</code> table reference
     */
    constructor() : this(DSL.name("site"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, SITE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class SitePath : Site, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): SitePath = SitePath(DSL.name(alias), this)
        override fun `as`(alias: Name): SitePath = SitePath(alias, this)
        override fun `as`(alias: Table<*>): SitePath = SitePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getIndexes(): List<Index> = listOf(SITE_GEOMETRIE_IDX)
    override fun getPrimaryKey(): UniqueKey<Record> = SITE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(SITE_SITE_CODE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(SITE__SITE_SITE_GESTIONNAIRE_ID_FKEY)

    private lateinit var _gestionnaire: GestionnairePath

    /**
     * Get the implicit join path to the <code>remocra.gestionnaire</code>
     * table.
     */
    fun gestionnaire(): GestionnairePath {
        if (!this::_gestionnaire.isInitialized) {
            _gestionnaire = GestionnairePath(this, SITE__SITE_SITE_GESTIONNAIRE_ID_FKEY, null)
        }

        return _gestionnaire
    }

    val gestionnaire: GestionnairePath
        get(): GestionnairePath = gestionnaire()

    private lateinit var _debitSimultane: DebitSimultanePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.debit_simultane</code> table
     */
    fun debitSimultane(): DebitSimultanePath {
        if (!this::_debitSimultane.isInitialized) {
            _debitSimultane = DebitSimultanePath(this, null, DEBIT_SIMULTANE__DEBIT_SIMULTANE_DEBIT_SIMULTANE_SITE_ID_FKEY.inverseKey)
        }

        return _debitSimultane
    }

    val debitSimultane: DebitSimultanePath
        get(): DebitSimultanePath = debitSimultane()

    private lateinit var _lContactGestionnaire: LContactGestionnairePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_contact_gestionnaire</code> table
     */
    fun lContactGestionnaire(): LContactGestionnairePath {
        if (!this::_lContactGestionnaire.isInitialized) {
            _lContactGestionnaire = LContactGestionnairePath(this, null, L_CONTACT_GESTIONNAIRE__L_CONTACT_GESTIONNAIRE_SITE_ID_FKEY.inverseKey)
        }

        return _lContactGestionnaire
    }

    val lContactGestionnaire: LContactGestionnairePath
        get(): LContactGestionnairePath = lContactGestionnaire()

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, null, PEI__PEI_PEI_SITE_ID_FKEY.inverseKey)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()
    override fun getChecks(): List<Check<Record>> = listOf(
        Internal.createCheck(this, DSL.name("polygon_multipolygon_site"), "(((geometrytype(site_geometrie) = 'POLYGON'::text) OR (geometrytype(site_geometrie) = 'MULTIPOLYGON'::text)))", true),
    )
    override fun `as`(alias: String): Site = Site(DSL.name(alias), this)
    override fun `as`(alias: Name): Site = Site(alias, this)
    override fun `as`(alias: Table<*>): Site = Site(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Site = Site(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Site = Site(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Site = Site(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Site = Site(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Site = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Site = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Site = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Site = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Site = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Site = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Site = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Site = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Site = where(DSL.notExists(select))
}
