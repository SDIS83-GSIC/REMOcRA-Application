/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables

import org.jooq.Check
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
import remocra.db.jooq.remocra.keys.PEI__PEI_PEI_CROISEMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.PEI__PEI_PEI_VOIE_ID_FKEY
import remocra.db.jooq.remocra.keys.VOIE_PKEY
import remocra.db.jooq.remocra.keys.VOIE__VOIE_VOIE_COMMUNE_ID_FKEY
import remocra.db.jooq.remocra.tables.Commune.CommunePath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
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
open class Voie(
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
         * The reference instance of <code>remocra.voie</code>
         */
        val VOIE: Voie = Voie()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.voie.voie_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("voie_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.voie.voie_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("voie_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.voie.voie_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("voie_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "", GeometryBinding())

    /**
     * The column <code>remocra.voie.voie_commune_id</code>.
     */
    val COMMUNE_ID: TableField<Record, UUID?> = createField(DSL.name("voie_commune_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.voie</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.voie</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.voie</code> table reference
     */
    constructor() : this(DSL.name("voie"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, VOIE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class VoiePath : Voie, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): VoiePath = VoiePath(DSL.name(alias), this)
        override fun `as`(alias: Name): VoiePath = VoiePath(alias, this)
        override fun `as`(alias: Table<*>): VoiePath = VoiePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = VOIE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(VOIE__VOIE_VOIE_COMMUNE_ID_FKEY)

    private lateinit var _commune: CommunePath

    /**
     * Get the implicit join path to the <code>remocra.commune</code> table.
     */
    fun commune(): CommunePath {
        if (!this::_commune.isInitialized) {
            _commune = CommunePath(this, VOIE__VOIE_VOIE_COMMUNE_ID_FKEY, null)
        }

        return _commune
    }

    val commune: CommunePath
        get(): CommunePath = commune()

    private lateinit var _peiPeiCroisementIdFkey: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table,
     * via the <code>pei_pei_croisement_id_fkey</code> key
     */
    fun peiPeiCroisementIdFkey(): PeiPath {
        if (!this::_peiPeiCroisementIdFkey.isInitialized) {
            _peiPeiCroisementIdFkey = PeiPath(this, null, PEI__PEI_PEI_CROISEMENT_ID_FKEY.inverseKey)
        }

        return _peiPeiCroisementIdFkey
    }

    val peiPeiCroisementIdFkey: PeiPath
        get(): PeiPath = peiPeiCroisementIdFkey()

    private lateinit var _peiPeiVoieIdFkey: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table,
     * via the <code>pei_pei_voie_id_fkey</code> key
     */
    fun peiPeiVoieIdFkey(): PeiPath {
        if (!this::_peiPeiVoieIdFkey.isInitialized) {
            _peiPeiVoieIdFkey = PeiPath(this, null, PEI__PEI_PEI_VOIE_ID_FKEY.inverseKey)
        }

        return _peiPeiVoieIdFkey
    }

    val peiPeiVoieIdFkey: PeiPath
        get(): PeiPath = peiPeiVoieIdFkey()
    override fun getChecks(): List<Check<Record>> = listOf(
        Internal.createCheck(this, DSL.name("line_or_multiline_voie"), "(((geometrytype(voie_geometrie) = 'LINESTRING'::text) OR (geometrytype(voie_geometrie) = 'MULTILINESTRING'::text)))", true),
    )
    override fun `as`(alias: String): Voie = Voie(DSL.name(alias), this)
    override fun `as`(alias: Name): Voie = Voie(alias, this)
    override fun `as`(alias: Table<*>): Voie = Voie(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Voie = Voie(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Voie = Voie(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Voie = Voie(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Voie = Voie(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Voie = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Voie = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Voie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Voie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Voie = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Voie = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Voie = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Voie = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Voie = where(DSL.notExists(select))
}
