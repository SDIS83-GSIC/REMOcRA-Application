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
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_MODULE__L_THEMATIQUE_MODULE_MODULE_ID_FKEY
import remocra.db.jooq.remocra.keys.MODULE_PKEY
import remocra.db.jooq.remocra.tables.LThematiqueModule.LThematiqueModulePath
import remocra.db.jooq.remocra.tables.Thematique.ThematiquePath
import java.util.UUID
import javax.annotation.processing.Generated
import kotlin.collections.Collection

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
open class Module(
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
         * The reference instance of <code>remocra.module</code>
         */
        val MODULE: Module = Module()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.module.module_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("module_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.module.module_type</code>.
     */
    val TYPE: TableField<Record, TypeModule?> = createField(DSL.name("module_type"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypeModule::class.java), this, "")

    /**
     * The column <code>remocra.module.module_titre</code>.
     */
    val TITRE: TableField<Record, String?> = createField(DSL.name("module_titre"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.module.module_image</code>. Chemin de l'image
     * relatif à /var/lib/remocra/images/accueil
     */
    val IMAGE: TableField<Record, String?> = createField(DSL.name("module_image"), SQLDataType.CLOB, this, "Chemin de l'image relatif à /var/lib/remocra/images/accueil")

    /**
     * The column <code>remocra.module.module_contenu_html</code>.
     */
    val CONTENU_HTML: TableField<Record, String?> = createField(DSL.name("module_contenu_html"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.module.module_colonne</code>.
     */
    val COLONNE: TableField<Record, Int?> = createField(DSL.name("module_colonne"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>remocra.module.module_ligne</code>.
     */
    val LIGNE: TableField<Record, Int?> = createField(DSL.name("module_ligne"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>remocra.module.module_nb_document</code>.
     */
    val NB_DOCUMENT: TableField<Record, Int?> = createField(DSL.name("module_nb_document"), SQLDataType.INTEGER, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.module</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.module</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.module</code> table reference
     */
    constructor() : this(DSL.name("module"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, MODULE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ModulePath : Module, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ModulePath = ModulePath(DSL.name(alias), this)
        override fun `as`(alias: Name): ModulePath = ModulePath(alias, this)
        override fun `as`(alias: Table<*>): ModulePath = ModulePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = MODULE_PKEY

    private lateinit var _lThematiqueModule: LThematiqueModulePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_module</code> table
     */
    fun lThematiqueModule(): LThematiqueModulePath {
        if (!this::_lThematiqueModule.isInitialized) {
            _lThematiqueModule = LThematiqueModulePath(this, null, L_THEMATIQUE_MODULE__L_THEMATIQUE_MODULE_MODULE_ID_FKEY.inverseKey)
        }

        return _lThematiqueModule
    }

    val lThematiqueModule: LThematiqueModulePath
        get(): LThematiqueModulePath = lThematiqueModule()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.thematique</code> table
     */
    val thematique: ThematiquePath
        get(): ThematiquePath = lThematiqueModule().thematique()
    override fun `as`(alias: String): Module = Module(DSL.name(alias), this)
    override fun `as`(alias: Name): Module = Module(alias, this)
    override fun `as`(alias: Table<*>): Module = Module(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Module = Module(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Module = Module(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Module = Module(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Module = Module(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Module = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Module = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Module = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Module = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Module = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Module = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Module = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Module = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Module = where(DSL.notExists(select))
}
