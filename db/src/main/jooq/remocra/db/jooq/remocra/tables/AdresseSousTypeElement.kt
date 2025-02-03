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
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.keys.ADRESSE_ELEMENT__ADRESSE_ELEMENT_ADRESSE_ELEMENT_SOUS_TYPE_FKEY
import remocra.db.jooq.remocra.keys.ADRESSE_SOUS_TYPE_ELEMENT_ADRESSE_SOUS_TYPE_ELEMENT_CODE_KEY
import remocra.db.jooq.remocra.keys.ADRESSE_SOUS_TYPE_ELEMENT_PKEY
import remocra.db.jooq.remocra.keys.ADRESSE_SOUS_TYPE_ELEMENT__ADRESSE_SOUS_TYPE_ELEMENT_ADRESSE_SOUS_TYPE_ELEMENT_TYPE_E_FKEY
import remocra.db.jooq.remocra.tables.AdresseElement.AdresseElementPath
import remocra.db.jooq.remocra.tables.AdresseTypeElement.AdresseTypeElementPath
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
open class AdresseSousTypeElement(
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
         * <code>remocra.adresse_sous_type_element</code>
         */
        val ADRESSE_SOUS_TYPE_ELEMENT: AdresseSousTypeElement = AdresseSousTypeElement()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("adresse_sous_type_element_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("adresse_sous_type_element_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("adresse_sous_type_element_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("adresse_sous_type_element_libelle"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_type_geometrie</code>.
     */
    val TYPE_GEOMETRIE: TableField<Record, TypeGeometry?> = createField(DSL.name("adresse_sous_type_element_type_geometrie"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypeGeometry::class.java), this, "")

    /**
     * The column
     * <code>remocra.adresse_sous_type_element.adresse_sous_type_element_type_element</code>.
     */
    val TYPE_ELEMENT: TableField<Record, UUID?> = createField(DSL.name("adresse_sous_type_element_type_element"), SQLDataType.UUID, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.adresse_sous_type_element</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.adresse_sous_type_element</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.adresse_sous_type_element</code> table reference
     */
    constructor() : this(DSL.name("adresse_sous_type_element"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, ADRESSE_SOUS_TYPE_ELEMENT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class AdresseSousTypeElementPath : AdresseSousTypeElement, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): AdresseSousTypeElementPath = AdresseSousTypeElementPath(DSL.name(alias), this)
        override fun `as`(alias: Name): AdresseSousTypeElementPath = AdresseSousTypeElementPath(alias, this)
        override fun `as`(alias: Table<*>): AdresseSousTypeElementPath = AdresseSousTypeElementPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = ADRESSE_SOUS_TYPE_ELEMENT_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(ADRESSE_SOUS_TYPE_ELEMENT_ADRESSE_SOUS_TYPE_ELEMENT_CODE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(ADRESSE_SOUS_TYPE_ELEMENT__ADRESSE_SOUS_TYPE_ELEMENT_ADRESSE_SOUS_TYPE_ELEMENT_TYPE_E_FKEY)

    private lateinit var _adresseTypeElement: AdresseTypeElementPath

    /**
     * Get the implicit join path to the
     * <code>remocra.adresse_type_element</code> table.
     */
    fun adresseTypeElement(): AdresseTypeElementPath {
        if (!this::_adresseTypeElement.isInitialized) {
            _adresseTypeElement = AdresseTypeElementPath(this, ADRESSE_SOUS_TYPE_ELEMENT__ADRESSE_SOUS_TYPE_ELEMENT_ADRESSE_SOUS_TYPE_ELEMENT_TYPE_E_FKEY, null)
        }

        return _adresseTypeElement
    }

    val adresseTypeElement: AdresseTypeElementPath
        get(): AdresseTypeElementPath = adresseTypeElement()

    private lateinit var _adresseElement: AdresseElementPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.adresse_element</code> table
     */
    fun adresseElement(): AdresseElementPath {
        if (!this::_adresseElement.isInitialized) {
            _adresseElement = AdresseElementPath(this, null, ADRESSE_ELEMENT__ADRESSE_ELEMENT_ADRESSE_ELEMENT_SOUS_TYPE_FKEY.inverseKey)
        }

        return _adresseElement
    }

    val adresseElement: AdresseElementPath
        get(): AdresseElementPath = adresseElement()
    override fun `as`(alias: String): AdresseSousTypeElement = AdresseSousTypeElement(DSL.name(alias), this)
    override fun `as`(alias: Name): AdresseSousTypeElement = AdresseSousTypeElement(alias, this)
    override fun `as`(alias: Table<*>): AdresseSousTypeElement = AdresseSousTypeElement(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): AdresseSousTypeElement = AdresseSousTypeElement(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): AdresseSousTypeElement = AdresseSousTypeElement(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): AdresseSousTypeElement = AdresseSousTypeElement(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): AdresseSousTypeElement = AdresseSousTypeElement(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): AdresseSousTypeElement = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): AdresseSousTypeElement = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): AdresseSousTypeElement = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): AdresseSousTypeElement = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): AdresseSousTypeElement = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): AdresseSousTypeElement = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): AdresseSousTypeElement = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): AdresseSousTypeElement = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): AdresseSousTypeElement = where(DSL.notExists(select))
}
