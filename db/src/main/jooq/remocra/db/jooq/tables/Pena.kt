/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables

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
import remocra.db.jooq.Remocra
import remocra.db.jooq.enums.Disponibilite
import remocra.db.jooq.enums.VolumeConstate
import remocra.db.jooq.keys.PENA_PKEY
import remocra.db.jooq.keys.PENA__PENA_PENA_ID_FKEY
import remocra.db.jooq.keys.PENA__PENA_PENA_MATERIAU_ID_FKEY
import remocra.db.jooq.keys.PIBI__PIBI_PIBI_PENA_ID_FKEY
import remocra.db.jooq.tables.Materiau.MateriauPath
import remocra.db.jooq.tables.Pei.PeiPath
import remocra.db.jooq.tables.Pibi.PibiPath
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
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
open class Pena(
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
         * The reference instance of <code>remocra.pena</code>
         */
        val PENA: Pena = Pena()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.pena.pena_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("pena_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.pena.pena_disponibilite_hbe</code>.
     */
    val DISPONIBILITE_HBE: TableField<Record, Disponibilite?> = createField(DSL.name("pena_disponibilite_hbe"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(Disponibilite::class.java), this, "")

    /**
     * The column <code>remocra.pena.pena_capacite</code>. En m³ (mètre cube)
     */
    val CAPACITE: TableField<Record, Int?> = createField(DSL.name("pena_capacite"), SQLDataType.INTEGER, this, "En m³ (mètre cube)")

    /**
     * The column <code>remocra.pena.pena_coordonne_dfci</code>.
     */
    val COORDONNE_DFCI: TableField<Record, String?> = createField(DSL.name("pena_coordonne_dfci"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.pena.pena_materiau_id</code>.
     */
    val MATERIAU_ID: TableField<Record, UUID?> = createField(DSL.name("pena_materiau_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>remocra.pena.pena_volume_constate</code>.
     */
    val VOLUME_CONSTATE: TableField<Record, VolumeConstate?> = createField(DSL.name("pena_volume_constate"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(VolumeConstate::class.java), this, "")

    /**
     * The column <code>remocra.pena.pena_capacite_illimitee</code>. Vaut VRAI
     * lorsqu'on considère que ce PENA possède une capacité illimitée
     */
    val CAPACITE_ILLIMITEE: TableField<Record, Boolean?> = createField(DSL.name("pena_capacite_illimitee"), SQLDataType.BOOLEAN, this, "Vaut VRAI lorsqu'on considère que ce PENA possède une capacité illimitée")

    /**
     * The column <code>remocra.pena.pena_capacite_incertaine</code>. Vaut VRAI
     * lorsqu'on n'est pas certain de la capacité du PENA
     */
    val CAPACITE_INCERTAINE: TableField<Record, Boolean?> = createField(DSL.name("pena_capacite_incertaine"), SQLDataType.BOOLEAN, this, "Vaut VRAI lorsqu'on n'est pas certain de la capacité du PENA")

    /**
     * The column <code>remocra.pena.pena_quantite_appoint</code>.
     */
    val QUANTITE_APPOINT: TableField<Record, Double?> = createField(DSL.name("pena_quantite_appoint"), SQLDataType.DOUBLE, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.pena</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.pena</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.pena</code> table reference
     */
    constructor() : this(DSL.name("pena"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, PENA, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class PenaPath : Pena, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): PenaPath = PenaPath(DSL.name(alias), this)
        override fun `as`(alias: Name): PenaPath = PenaPath(alias, this)
        override fun `as`(alias: Table<*>): PenaPath = PenaPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = PENA_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(PENA__PENA_PENA_ID_FKEY, PENA__PENA_PENA_MATERIAU_ID_FKEY)

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit join path to the <code>remocra.pei</code> table.
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, PENA__PENA_PENA_ID_FKEY, null)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()

    private lateinit var _materiau: MateriauPath

    /**
     * Get the implicit join path to the <code>remocra.materiau</code> table.
     */
    fun materiau(): MateriauPath {
        if (!this::_materiau.isInitialized) {
            _materiau = MateriauPath(this, PENA__PENA_PENA_MATERIAU_ID_FKEY, null)
        }

        return _materiau
    }

    val materiau: MateriauPath
        get(): MateriauPath = materiau()

    private lateinit var _pibi: PibiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pibi</code> table
     */
    fun pibi(): PibiPath {
        if (!this::_pibi.isInitialized) {
            _pibi = PibiPath(this, null, PIBI__PIBI_PIBI_PENA_ID_FKEY.inverseKey)
        }

        return _pibi
    }

    val pibi: PibiPath
        get(): PibiPath = pibi()
    override fun `as`(alias: String): Pena = Pena(DSL.name(alias), this)
    override fun `as`(alias: Name): Pena = Pena(alias, this)
    override fun `as`(alias: Table<*>): Pena = Pena(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Pena = Pena(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Pena = Pena(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Pena = Pena(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Pena = Pena(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Pena = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Pena = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Pena = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Pena = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Pena = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Pena = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Pena = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Pena = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Pena = where(DSL.notExists(select))
}
