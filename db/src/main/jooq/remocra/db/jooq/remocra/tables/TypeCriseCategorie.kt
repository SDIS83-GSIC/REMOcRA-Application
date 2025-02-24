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
import remocra.db.jooq.remocra.keys.EVENEMENT__EVENEMENT_EVENEMENT_TYPE_CRISE_CATEGORIE_ID_FKEY
import remocra.db.jooq.remocra.keys.TYPE_CRISE_CATEGORIE_PKEY
import remocra.db.jooq.remocra.keys.TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_CODE_KEY
import remocra.db.jooq.remocra.keys.TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_LIBELLE_KEY
import remocra.db.jooq.remocra.keys.TYPE_CRISE_CATEGORIE__TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_CRISE_CATEGORIE__FKEY
import remocra.db.jooq.remocra.tables.CriseCategorie.CriseCategoriePath
import remocra.db.jooq.remocra.tables.Evenement.EvenementPath
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
open class TypeCriseCategorie(
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
         * The reference instance of <code>remocra.type_crise_categorie</code>
         */
        val TYPE_CRISE_CATEGORIE: TypeCriseCategorie = TypeCriseCategorie()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.type_crise_categorie.type_crise_categorie_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("type_crise_categorie_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_crise_categorie.type_crise_categorie_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("type_crise_categorie_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_crise_categorie.type_crise_categorie_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("type_crise_categorie_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.type_crise_categorie.type_crise_categorie_type_geometrie</code>.
     */
    val TYPE_GEOMETRIE: TableField<Record, TypeGeometry?> = createField(DSL.name("type_crise_categorie_type_geometrie"), SQLDataType.VARCHAR.asEnumDataType(TypeGeometry::class.java), this, "")

    /**
     * The column
     * <code>remocra.type_crise_categorie.type_crise_categorie_crise_categorie_id</code>.
     */
    val CRISE_CATEGORIE_ID: TableField<Record, UUID?> = createField(DSL.name("type_crise_categorie_crise_categorie_id"), SQLDataType.UUID, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.type_crise_categorie</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.type_crise_categorie</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.type_crise_categorie</code> table reference
     */
    constructor() : this(DSL.name("type_crise_categorie"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, TYPE_CRISE_CATEGORIE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class TypeCriseCategoriePath : TypeCriseCategorie, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): TypeCriseCategoriePath = TypeCriseCategoriePath(DSL.name(alias), this)
        override fun `as`(alias: Name): TypeCriseCategoriePath = TypeCriseCategoriePath(alias, this)
        override fun `as`(alias: Table<*>): TypeCriseCategoriePath = TypeCriseCategoriePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = TYPE_CRISE_CATEGORIE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_CODE_KEY, TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_LIBELLE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(TYPE_CRISE_CATEGORIE__TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_CRISE_CATEGORIE__FKEY)

    private lateinit var _criseCategorie: CriseCategoriePath

    /**
     * Get the implicit join path to the <code>remocra.crise_categorie</code>
     * table.
     */
    fun criseCategorie(): CriseCategoriePath {
        if (!this::_criseCategorie.isInitialized) {
            _criseCategorie = CriseCategoriePath(this, TYPE_CRISE_CATEGORIE__TYPE_CRISE_CATEGORIE_TYPE_CRISE_CATEGORIE_CRISE_CATEGORIE__FKEY, null)
        }

        return _criseCategorie
    }

    val criseCategorie: CriseCategoriePath
        get(): CriseCategoriePath = criseCategorie()

    private lateinit var _evenement: EvenementPath

    /**
     * Get the implicit to-many join path to the <code>remocra.evenement</code>
     * table
     */
    fun evenement(): EvenementPath {
        if (!this::_evenement.isInitialized) {
            _evenement = EvenementPath(this, null, EVENEMENT__EVENEMENT_EVENEMENT_TYPE_CRISE_CATEGORIE_ID_FKEY.inverseKey)
        }

        return _evenement
    }

    val evenement: EvenementPath
        get(): EvenementPath = evenement()
    override fun `as`(alias: String): TypeCriseCategorie = TypeCriseCategorie(DSL.name(alias), this)
    override fun `as`(alias: Name): TypeCriseCategorie = TypeCriseCategorie(alias, this)
    override fun `as`(alias: Table<*>): TypeCriseCategorie = TypeCriseCategorie(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): TypeCriseCategorie = TypeCriseCategorie(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): TypeCriseCategorie = TypeCriseCategorie(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): TypeCriseCategorie = TypeCriseCategorie(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): TypeCriseCategorie = TypeCriseCategorie(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): TypeCriseCategorie = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): TypeCriseCategorie = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): TypeCriseCategorie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): TypeCriseCategorie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): TypeCriseCategorie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): TypeCriseCategorie = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): TypeCriseCategorie = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): TypeCriseCategorie = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): TypeCriseCategorie = where(DSL.notExists(select))
}
