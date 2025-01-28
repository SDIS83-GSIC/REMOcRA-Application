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
import remocra.db.jooq.remocra.keys.CADASTRE_PARCELLE_PKEY
import remocra.db.jooq.remocra.keys.CADASTRE_PARCELLE__CADASTRE_PARCELLE_CADASTRE_PARCELLE_CADASTRE_SECTION_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PERMIS_CADASTRE_PARCELLE__L_PERMIS_CADASTRE_PARCELLE_CADASTRE_PARCELLE_ID_FKEY
import remocra.db.jooq.remocra.keys.OLDEB__OLDEB_OLDEB_CADASTRE_PARCELLE_ID_FKEY
import remocra.db.jooq.remocra.tables.CadastreSection.CadastreSectionPath
import remocra.db.jooq.remocra.tables.LPermisCadastreParcelle.LPermisCadastreParcellePath
import remocra.db.jooq.remocra.tables.Oldeb.OldebPath
import remocra.db.jooq.remocra.tables.Permis.PermisPath
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
open class CadastreParcelle(
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
         * The reference instance of <code>remocra.cadastre_parcelle</code>
         */
        val CADASTRE_PARCELLE: CadastreParcelle = CadastreParcelle()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.cadastre_parcelle.cadastre_parcelle_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("cadastre_parcelle_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.cadastre_parcelle.cadastre_parcelle_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("cadastre_parcelle_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "", GeometryBinding())

    /**
     * The column
     * <code>remocra.cadastre_parcelle.cadastre_parcelle_numero</code>.
     */
    val NUMERO: TableField<Record, String?> = createField(DSL.name("cadastre_parcelle_numero"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.cadastre_parcelle.cadastre_parcelle_cadastre_section_id</code>.
     */
    val CADASTRE_SECTION_ID: TableField<Record, UUID?> = createField(DSL.name("cadastre_parcelle_cadastre_section_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.cadastre_parcelle</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.cadastre_parcelle</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.cadastre_parcelle</code> table reference
     */
    constructor() : this(DSL.name("cadastre_parcelle"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, CADASTRE_PARCELLE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class CadastreParcellePath : CadastreParcelle, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): CadastreParcellePath = CadastreParcellePath(DSL.name(alias), this)
        override fun `as`(alias: Name): CadastreParcellePath = CadastreParcellePath(alias, this)
        override fun `as`(alias: Table<*>): CadastreParcellePath = CadastreParcellePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = CADASTRE_PARCELLE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(CADASTRE_PARCELLE__CADASTRE_PARCELLE_CADASTRE_PARCELLE_CADASTRE_SECTION_ID_FKEY)

    private lateinit var _cadastreSection: CadastreSectionPath

    /**
     * Get the implicit join path to the <code>remocra.cadastre_section</code>
     * table.
     */
    fun cadastreSection(): CadastreSectionPath {
        if (!this::_cadastreSection.isInitialized) {
            _cadastreSection = CadastreSectionPath(this, CADASTRE_PARCELLE__CADASTRE_PARCELLE_CADASTRE_PARCELLE_CADASTRE_SECTION_ID_FKEY, null)
        }

        return _cadastreSection
    }

    val cadastreSection: CadastreSectionPath
        get(): CadastreSectionPath = cadastreSection()

    private lateinit var _lPermisCadastreParcelle: LPermisCadastreParcellePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_permis_cadastre_parcelle</code> table
     */
    fun lPermisCadastreParcelle(): LPermisCadastreParcellePath {
        if (!this::_lPermisCadastreParcelle.isInitialized) {
            _lPermisCadastreParcelle = LPermisCadastreParcellePath(this, null, L_PERMIS_CADASTRE_PARCELLE__L_PERMIS_CADASTRE_PARCELLE_CADASTRE_PARCELLE_ID_FKEY.inverseKey)
        }

        return _lPermisCadastreParcelle
    }

    val lPermisCadastreParcelle: LPermisCadastreParcellePath
        get(): LPermisCadastreParcellePath = lPermisCadastreParcelle()

    private lateinit var _oldeb: OldebPath

    /**
     * Get the implicit to-many join path to the <code>remocra.oldeb</code>
     * table
     */
    fun oldeb(): OldebPath {
        if (!this::_oldeb.isInitialized) {
            _oldeb = OldebPath(this, null, OLDEB__OLDEB_OLDEB_CADASTRE_PARCELLE_ID_FKEY.inverseKey)
        }

        return _oldeb
    }

    val oldeb: OldebPath
        get(): OldebPath = oldeb()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.permis</code> table
     */
    val permis: PermisPath
        get(): PermisPath = lPermisCadastreParcelle().permis()
    override fun `as`(alias: String): CadastreParcelle = CadastreParcelle(DSL.name(alias), this)
    override fun `as`(alias: Name): CadastreParcelle = CadastreParcelle(alias, this)
    override fun `as`(alias: Table<*>): CadastreParcelle = CadastreParcelle(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): CadastreParcelle = CadastreParcelle(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): CadastreParcelle = CadastreParcelle(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): CadastreParcelle = CadastreParcelle(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): CadastreParcelle = CadastreParcelle(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): CadastreParcelle = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): CadastreParcelle = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): CadastreParcelle = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): CadastreParcelle = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): CadastreParcelle = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): CadastreParcelle = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): CadastreParcelle = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): CadastreParcelle = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): CadastreParcelle = where(DSL.notExists(select))
}
