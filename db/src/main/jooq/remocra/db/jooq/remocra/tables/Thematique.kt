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
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_COURRIER__L_THEMATIQUE_COURRIER_THEMATIQUE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_DOCUMENT_HABILITABLE__L_THEMATIQUE_DOCUMENT_HABILITABLE_THEMATIQUE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_MODULE__L_THEMATIQUE_MODULE_THEMATIQUE_ID_FKEY
import remocra.db.jooq.remocra.keys.THEMATIQUE_PKEY
import remocra.db.jooq.remocra.keys.THEMATIQUE_THEMATIQUE_CODE_KEY
import remocra.db.jooq.remocra.tables.Courrier.CourrierPath
import remocra.db.jooq.remocra.tables.DocumentHabilitable.DocumentHabilitablePath
import remocra.db.jooq.remocra.tables.LThematiqueCourrier.LThematiqueCourrierPath
import remocra.db.jooq.remocra.tables.LThematiqueDocumentHabilitable.LThematiqueDocumentHabilitablePath
import remocra.db.jooq.remocra.tables.LThematiqueModule.LThematiqueModulePath
import remocra.db.jooq.remocra.tables.Module.ModulePath
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
open class Thematique(
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
         * The reference instance of <code>remocra.thematique</code>
         */
        val THEMATIQUE: Thematique = Thematique()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.thematique.thematique_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("thematique_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.thematique.thematique_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("thematique_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.thematique.thematique_protected</code>.
     */
    val PROTECTED: TableField<Record, Boolean?> = createField(DSL.name("thematique_protected"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.thematique.thematique_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("thematique_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.thematique.thematique_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("thematique_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.thematique</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.thematique</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.thematique</code> table reference
     */
    constructor() : this(DSL.name("thematique"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, THEMATIQUE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ThematiquePath : Thematique, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ThematiquePath = ThematiquePath(DSL.name(alias), this)
        override fun `as`(alias: Name): ThematiquePath = ThematiquePath(alias, this)
        override fun `as`(alias: Table<*>): ThematiquePath = ThematiquePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = THEMATIQUE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(THEMATIQUE_THEMATIQUE_CODE_KEY)

    private lateinit var _lThematiqueCourrier: LThematiqueCourrierPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_courrier</code> table
     */
    fun lThematiqueCourrier(): LThematiqueCourrierPath {
        if (!this::_lThematiqueCourrier.isInitialized) {
            _lThematiqueCourrier = LThematiqueCourrierPath(this, null, L_THEMATIQUE_COURRIER__L_THEMATIQUE_COURRIER_THEMATIQUE_ID_FKEY.inverseKey)
        }

        return _lThematiqueCourrier
    }

    val lThematiqueCourrier: LThematiqueCourrierPath
        get(): LThematiqueCourrierPath = lThematiqueCourrier()

    private lateinit var _lThematiqueDocumentHabilitable: LThematiqueDocumentHabilitablePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_document_habilitable</code> table
     */
    fun lThematiqueDocumentHabilitable(): LThematiqueDocumentHabilitablePath {
        if (!this::_lThematiqueDocumentHabilitable.isInitialized) {
            _lThematiqueDocumentHabilitable = LThematiqueDocumentHabilitablePath(this, null, L_THEMATIQUE_DOCUMENT_HABILITABLE__L_THEMATIQUE_DOCUMENT_HABILITABLE_THEMATIQUE_ID_FKEY.inverseKey)
        }

        return _lThematiqueDocumentHabilitable
    }

    val lThematiqueDocumentHabilitable: LThematiqueDocumentHabilitablePath
        get(): LThematiqueDocumentHabilitablePath = lThematiqueDocumentHabilitable()

    private lateinit var _lThematiqueModule: LThematiqueModulePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_module</code> table
     */
    fun lThematiqueModule(): LThematiqueModulePath {
        if (!this::_lThematiqueModule.isInitialized) {
            _lThematiqueModule = LThematiqueModulePath(this, null, L_THEMATIQUE_MODULE__L_THEMATIQUE_MODULE_THEMATIQUE_ID_FKEY.inverseKey)
        }

        return _lThematiqueModule
    }

    val lThematiqueModule: LThematiqueModulePath
        get(): LThematiqueModulePath = lThematiqueModule()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.courrier</code> table
     */
    val courrier: CourrierPath
        get(): CourrierPath = lThematiqueCourrier().courrier()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.document_habilitable</code> table
     */
    val documentHabilitable: DocumentHabilitablePath
        get(): DocumentHabilitablePath = lThematiqueDocumentHabilitable().documentHabilitable()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.module</code> table
     */
    val module: ModulePath
        get(): ModulePath = lThematiqueModule().module()
    override fun `as`(alias: String): Thematique = Thematique(DSL.name(alias), this)
    override fun `as`(alias: Name): Thematique = Thematique(alias, this)
    override fun `as`(alias: Table<*>): Thematique = Thematique(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Thematique = Thematique(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Thematique = Thematique(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Thematique = Thematique(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Thematique = Thematique(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Thematique = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Thematique = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Thematique = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Thematique = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Thematique = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Thematique = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Thematique = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Thematique = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Thematique = where(DSL.notExists(select))
}
