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
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.db.jooq.remocra.keys.RAPPORT_PERSONNALISE_PARAMETRE_PKEY
import remocra.db.jooq.remocra.keys.RAPPORT_PERSONNALISE_PARAMETRE__RAPPORT_PERSONNALISE_PARAMETR_RAPPORT_PERSONNALISE_PARAMET_FKEY
import remocra.db.jooq.remocra.tables.RapportPersonnalise.RapportPersonnalisePath
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
open class RapportPersonnaliseParametre(
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
         * The reference instance of
         * <code>remocra.rapport_personnalise_parametre</code>
         */
        val RAPPORT_PERSONNALISE_PARAMETRE: RapportPersonnaliseParametre = RapportPersonnaliseParametre()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("rapport_personnalise_parametre_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_rapport_personnalise_id</code>.
     */
    val RAPPORT_PERSONNALISE_ID: TableField<Record, UUID?> = createField(DSL.name("rapport_personnalise_parametre_rapport_personnalise_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_source_sql</code>.
     */
    val SOURCE_SQL: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_source_sql"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_description</code>.
     */
    val DESCRIPTION: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_description"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_source_sql_id</code>.
     */
    val SOURCE_SQL_ID: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_source_sql_id"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_source_sql_libelle</code>.
     */
    val SOURCE_SQL_LIBELLE: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_source_sql_libelle"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_valeur_defaut</code>.
     */
    val VALEUR_DEFAUT: TableField<Record, String?> = createField(DSL.name("rapport_personnalise_parametre_valeur_defaut"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_is_required</code>.
     */
    val IS_REQUIRED: TableField<Record, Boolean?> = createField(DSL.name("rapport_personnalise_parametre_is_required"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_type</code>.
     */
    val TYPE: TableField<Record, TypeParametreRapportPersonnalise?> = createField(DSL.name("rapport_personnalise_parametre_type"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypeParametreRapportPersonnalise::class.java), this, "")

    /**
     * The column
     * <code>remocra.rapport_personnalise_parametre.rapport_personnalise_parametre_ordre</code>.
     */
    val ORDRE: TableField<Record, Int?> = createField(DSL.name("rapport_personnalise_parametre_ordre"), SQLDataType.INTEGER.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.rapport_personnalise_parametre</code>
     * table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.rapport_personnalise_parametre</code>
     * table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.rapport_personnalise_parametre</code> table
     * reference
     */
    constructor() : this(DSL.name("rapport_personnalise_parametre"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, RAPPORT_PERSONNALISE_PARAMETRE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class RapportPersonnaliseParametrePath : RapportPersonnaliseParametre, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): RapportPersonnaliseParametrePath = RapportPersonnaliseParametrePath(DSL.name(alias), this)
        override fun `as`(alias: Name): RapportPersonnaliseParametrePath = RapportPersonnaliseParametrePath(alias, this)
        override fun `as`(alias: Table<*>): RapportPersonnaliseParametrePath = RapportPersonnaliseParametrePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = RAPPORT_PERSONNALISE_PARAMETRE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(RAPPORT_PERSONNALISE_PARAMETRE__RAPPORT_PERSONNALISE_PARAMETR_RAPPORT_PERSONNALISE_PARAMET_FKEY)

    private lateinit var _rapportPersonnalise: RapportPersonnalisePath

    /**
     * Get the implicit join path to the
     * <code>remocra.rapport_personnalise</code> table.
     */
    fun rapportPersonnalise(): RapportPersonnalisePath {
        if (!this::_rapportPersonnalise.isInitialized) {
            _rapportPersonnalise = RapportPersonnalisePath(this, RAPPORT_PERSONNALISE_PARAMETRE__RAPPORT_PERSONNALISE_PARAMETR_RAPPORT_PERSONNALISE_PARAMET_FKEY, null)
        }

        return _rapportPersonnalise
    }

    val rapportPersonnalise: RapportPersonnalisePath
        get(): RapportPersonnalisePath = rapportPersonnalise()
    override fun `as`(alias: String): RapportPersonnaliseParametre = RapportPersonnaliseParametre(DSL.name(alias), this)
    override fun `as`(alias: Name): RapportPersonnaliseParametre = RapportPersonnaliseParametre(alias, this)
    override fun `as`(alias: Table<*>): RapportPersonnaliseParametre = RapportPersonnaliseParametre(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): RapportPersonnaliseParametre = RapportPersonnaliseParametre(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): RapportPersonnaliseParametre = RapportPersonnaliseParametre(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): RapportPersonnaliseParametre = RapportPersonnaliseParametre(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): RapportPersonnaliseParametre = RapportPersonnaliseParametre(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): RapportPersonnaliseParametre = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): RapportPersonnaliseParametre = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): RapportPersonnaliseParametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): RapportPersonnaliseParametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): RapportPersonnaliseParametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): RapportPersonnaliseParametre = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): RapportPersonnaliseParametre = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): RapportPersonnaliseParametre = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): RapportPersonnaliseParametre = where(DSL.notExists(select))
}
