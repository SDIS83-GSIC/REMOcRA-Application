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
import remocra.db.jooq.remocra.keys.COUCHE_COUCHE_CODE_KEY
import remocra.db.jooq.remocra.keys.COUCHE_COUCHE_ORDRE_KEY
import remocra.db.jooq.remocra.keys.COUCHE_PKEY
import remocra.db.jooq.remocra.keys.COUCHE__COUCHE_COUCHE_GROUPE_COUCHE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_COUCHE_DROIT__L_COUCHE_DROIT_COUCHE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_COUCHE_MODULE__L_COUCHE_MODULE_COUCHE_ID_FKEY
import remocra.db.jooq.remocra.tables.GroupeCouche.GroupeCouchePath
import remocra.db.jooq.remocra.tables.LCoucheDroit.LCoucheDroitPath
import remocra.db.jooq.remocra.tables.LCoucheModule.LCoucheModulePath
import remocra.db.jooq.remocra.tables.ProfilDroit.ProfilDroitPath
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
open class Couche(
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
         * The reference instance of <code>remocra.couche</code>
         */
        val COUCHE: Couche = Couche()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.couche.couche_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("couche_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.couche.couche_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("couche_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.couche.couche_groupe_couche_id</code>.
     */
    val GROUPE_COUCHE_ID: TableField<Record, UUID?> = createField(DSL.name("couche_groupe_couche_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.couche.couche_ordre</code>.
     */
    val ORDRE: TableField<Record, Int?> = createField(DSL.name("couche_ordre"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>remocra.couche.couche_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("couche_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.couche.couche_source</code>. Type de la couche
     * (WMS, WMTS, etc.)
     */
    val SOURCE: TableField<Record, String?> = createField(DSL.name("couche_source"), SQLDataType.CLOB.nullable(false), this, "Type de la couche (WMS, WMTS, etc.)")

    /**
     * The column <code>remocra.couche.couche_projection</code>. Référentiel de
     * la couche
     */
    val PROJECTION: TableField<Record, String?> = createField(DSL.name("couche_projection"), SQLDataType.CLOB.nullable(false), this, "Référentiel de la couche")

    /**
     * The column <code>remocra.couche.couche_url</code>. URL de la source
     * absolu si externe sinon relatif
     */
    val URL: TableField<Record, String?> = createField(DSL.name("couche_url"), SQLDataType.CLOB.nullable(false), this, "URL de la source absolu si externe sinon relatif")

    /**
     * The column <code>remocra.couche.couche_nom</code>. Nom de la couche issue
     * de la source
     */
    val NOM: TableField<Record, String?> = createField(DSL.name("couche_nom"), SQLDataType.CLOB.nullable(false), this, "Nom de la couche issue de la source")

    /**
     * The column <code>remocra.couche.couche_format</code>. Format de la couche
     */
    val FORMAT: TableField<Record, String?> = createField(DSL.name("couche_format"), SQLDataType.CLOB.nullable(false), this, "Format de la couche")

    /**
     * The column <code>remocra.couche.couche_public</code>. Indique si la
     * couche est accessible publiquement
     */
    val PUBLIC: TableField<Record, Boolean?> = createField(DSL.name("couche_public"), SQLDataType.BOOLEAN.nullable(false), this, "Indique si la couche est accessible publiquement")

    /**
     * The column <code>remocra.couche.couche_active</code>. Affichage par
     * défaut oui/non
     */
    val ACTIVE: TableField<Record, Boolean?> = createField(DSL.name("couche_active"), SQLDataType.BOOLEAN.nullable(false), this, "Affichage par défaut oui/non")

    /**
     * The column <code>remocra.couche.couche_icone</code>. Icône de la couche
     */
    val ICONE: TableField<Record, ByteArray?> = createField(DSL.name("couche_icone"), SQLDataType.BLOB, this, "Icône de la couche")

    /**
     * The column <code>remocra.couche.couche_legende</code>. Image de la
     * légende de la couche
     */
    val LEGENDE: TableField<Record, ByteArray?> = createField(DSL.name("couche_legende"), SQLDataType.BLOB, this, "Image de la légende de la couche")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.couche</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.couche</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.couche</code> table reference
     */
    constructor() : this(DSL.name("couche"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, COUCHE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class CouchePath : Couche, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): CouchePath = CouchePath(DSL.name(alias), this)
        override fun `as`(alias: Name): CouchePath = CouchePath(alias, this)
        override fun `as`(alias: Table<*>): CouchePath = CouchePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = COUCHE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(COUCHE_COUCHE_CODE_KEY, COUCHE_COUCHE_ORDRE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(COUCHE__COUCHE_COUCHE_GROUPE_COUCHE_ID_FKEY)

    private lateinit var _groupeCouche: GroupeCouchePath

    /**
     * Get the implicit join path to the <code>remocra.groupe_couche</code>
     * table.
     */
    fun groupeCouche(): GroupeCouchePath {
        if (!this::_groupeCouche.isInitialized) {
            _groupeCouche = GroupeCouchePath(this, COUCHE__COUCHE_COUCHE_GROUPE_COUCHE_ID_FKEY, null)
        }

        return _groupeCouche
    }

    val groupeCouche: GroupeCouchePath
        get(): GroupeCouchePath = groupeCouche()

    private lateinit var _lCoucheDroit: LCoucheDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_couche_droit</code> table
     */
    fun lCoucheDroit(): LCoucheDroitPath {
        if (!this::_lCoucheDroit.isInitialized) {
            _lCoucheDroit = LCoucheDroitPath(this, null, L_COUCHE_DROIT__L_COUCHE_DROIT_COUCHE_ID_FKEY.inverseKey)
        }

        return _lCoucheDroit
    }

    val lCoucheDroit: LCoucheDroitPath
        get(): LCoucheDroitPath = lCoucheDroit()

    private lateinit var _lCoucheModule: LCoucheModulePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_couche_module</code> table
     */
    fun lCoucheModule(): LCoucheModulePath {
        if (!this::_lCoucheModule.isInitialized) {
            _lCoucheModule = LCoucheModulePath(this, null, L_COUCHE_MODULE__L_COUCHE_MODULE_COUCHE_ID_FKEY.inverseKey)
        }

        return _lCoucheModule
    }

    val lCoucheModule: LCoucheModulePath
        get(): LCoucheModulePath = lCoucheModule()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.profil_droit</code> table
     */
    val profilDroit: ProfilDroitPath
        get(): ProfilDroitPath = lCoucheDroit().profilDroit()
    override fun `as`(alias: String): Couche = Couche(DSL.name(alias), this)
    override fun `as`(alias: Name): Couche = Couche(alias, this)
    override fun `as`(alias: Table<*>): Couche = Couche(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Couche = Couche(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Couche = Couche(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Couche = Couche(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Couche = Couche(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Couche = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Couche = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Couche = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Couche = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Couche = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Couche = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Couche = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Couche = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Couche = where(DSL.notExists(select))
}
