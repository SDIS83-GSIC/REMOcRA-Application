/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
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
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.enums.TypeParametre
import remocra.db.jooq.remocra.keys.PARAMETRE_PARAMETRE_CODE_KEY
import remocra.db.jooq.remocra.keys.PARAMETRE_PKEY
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
open class Parametre(
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
         * The reference instance of <code>remocra.parametre</code>
         */
        val PARAMETRE: Parametre = Parametre()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.parametre.parametre_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("parametre_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.parametre.parametre_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("parametre_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.parametre.parametre_valeur</code>.
     */
    val VALEUR: TableField<Record, String?> = createField(DSL.name("parametre_valeur"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.parametre.parametre_type</code>.
     */
    val TYPE: TableField<Record, TypeParametre?> = createField(DSL.name("parametre_type"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypeParametre::class.java), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.parametre</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.parametre</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.parametre</code> table reference
     */
    constructor() : this(DSL.name("parametre"), null)
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = PARAMETRE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(PARAMETRE_PARAMETRE_CODE_KEY)
    override fun `as`(alias: String): Parametre = Parametre(DSL.name(alias), this)
    override fun `as`(alias: Name): Parametre = Parametre(alias, this)
    override fun `as`(alias: Table<*>): Parametre = Parametre(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Parametre = Parametre(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Parametre = Parametre(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Parametre = Parametre(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Parametre = Parametre(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Parametre = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Parametre = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Parametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Parametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Parametre = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Parametre = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Parametre = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Parametre = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Parametre = where(DSL.notExists(select))
}
