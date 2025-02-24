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
import remocra.db.jooq.bindings.ZonedDateTimeBinding
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.db.jooq.remocra.keys.CRISE_PKEY
import remocra.db.jooq.remocra.keys.CRISE__CRISE_CRISE_TYPE_CRISE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_CRISE_COMMUNE__L_CRISE_COMMUNE_CRISE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_TOPONYMIE_CRISE__L_TOPONYMIE_CRISE_CRISE_ID_FKEY
import remocra.db.jooq.remocra.tables.LCriseCommune.LCriseCommunePath
import remocra.db.jooq.remocra.tables.LToponymieCrise.LToponymieCrisePath
import remocra.db.jooq.remocra.tables.TypeCrise.TypeCrisePath
import java.time.ZonedDateTime
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
open class Crise(
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
         * The reference instance of <code>remocra.crise</code>
         */
        val CRISE: Crise = Crise()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.crise.crise_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("crise_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.crise.crise_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("crise_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.crise.crise_description</code>.
     */
    val DESCRIPTION: TableField<Record, String?> = createField(DSL.name("crise_description"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.crise.crise_date_debut</code>.
     */
    val DATE_DEBUT: TableField<Record, ZonedDateTime?> = createField(DSL.name("crise_date_debut"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "", ZonedDateTimeBinding())

    /**
     * The column <code>remocra.crise.crise_date_fin</code>.
     */
    val DATE_FIN: TableField<Record, ZonedDateTime?> = createField(DSL.name("crise_date_fin"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "", ZonedDateTimeBinding())

    /**
     * The column <code>remocra.crise.crise_type_crise_id</code>.
     */
    val TYPE_CRISE_ID: TableField<Record, UUID?> = createField(DSL.name("crise_type_crise_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.crise.crise_statut_type</code>.
     */
    val STATUT_TYPE: TableField<Record, TypeCriseStatut?> = createField(DSL.name("crise_statut_type"), SQLDataType.VARCHAR.nullable(false).defaultValue(DSL.field(DSL.raw("'EN_COURS'::type_crise_statut"), SQLDataType.VARCHAR)).asEnumDataType(TypeCriseStatut::class.java), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.crise</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.crise</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.crise</code> table reference
     */
    constructor() : this(DSL.name("crise"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, CRISE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class CrisePath : Crise, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): CrisePath = CrisePath(DSL.name(alias), this)
        override fun `as`(alias: Name): CrisePath = CrisePath(alias, this)
        override fun `as`(alias: Table<*>): CrisePath = CrisePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = CRISE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(CRISE__CRISE_CRISE_TYPE_CRISE_ID_FKEY)

    private lateinit var _typeCrise: TypeCrisePath

    /**
     * Get the implicit join path to the <code>remocra.type_crise</code> table.
     */
    fun typeCrise(): TypeCrisePath {
        if (!this::_typeCrise.isInitialized) {
            _typeCrise = TypeCrisePath(this, CRISE__CRISE_CRISE_TYPE_CRISE_ID_FKEY, null)
        }

        return _typeCrise
    }

    val typeCrise: TypeCrisePath
        get(): TypeCrisePath = typeCrise()

    private lateinit var _lCriseCommune: LCriseCommunePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_crise_commune</code> table
     */
    fun lCriseCommune(): LCriseCommunePath {
        if (!this::_lCriseCommune.isInitialized) {
            _lCriseCommune = LCriseCommunePath(this, null, L_CRISE_COMMUNE__L_CRISE_COMMUNE_CRISE_ID_FKEY.inverseKey)
        }

        return _lCriseCommune
    }

    val lCriseCommune: LCriseCommunePath
        get(): LCriseCommunePath = lCriseCommune()

    private lateinit var _lToponymieCrise: LToponymieCrisePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_toponymie_crise</code> table
     */
    fun lToponymieCrise(): LToponymieCrisePath {
        if (!this::_lToponymieCrise.isInitialized) {
            _lToponymieCrise = LToponymieCrisePath(this, null, L_TOPONYMIE_CRISE__L_TOPONYMIE_CRISE_CRISE_ID_FKEY.inverseKey)
        }

        return _lToponymieCrise
    }

    val lToponymieCrise: LToponymieCrisePath
        get(): LToponymieCrisePath = lToponymieCrise()
    override fun `as`(alias: String): Crise = Crise(DSL.name(alias), this)
    override fun `as`(alias: Name): Crise = Crise(alias, this)
    override fun `as`(alias: Table<*>): Crise = Crise(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Crise = Crise(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Crise = Crise(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Crise = Crise(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Crise = Crise(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Crise = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Crise = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Crise = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Crise = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Crise = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Crise = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Crise = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Crise = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Crise = where(DSL.notExists(select))
}
