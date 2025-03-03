/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.couverturehydraulique.tables

import org.jooq.Check
import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
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
import remocra.db.jooq.couverturehydraulique.Couverturehydraulique
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.couverturehydraulique.indexes.PEI_PROJET_GEOMETRIE_IDX
import remocra.db.jooq.couverturehydraulique.keys.PEI_PROJET_PKEY
import remocra.db.jooq.couverturehydraulique.keys.PEI_PROJET__PEI_PROJET_PEI_PROJET_DIAMETRE_ID_FKEY
import remocra.db.jooq.couverturehydraulique.keys.PEI_PROJET__PEI_PROJET_PEI_PROJET_ETUDE_ID_FKEY
import remocra.db.jooq.couverturehydraulique.keys.PEI_PROJET__PEI_PROJET_PEI_PROJET_NATURE_DECI_ID_FKEY
import remocra.db.jooq.couverturehydraulique.tables.Etude.EtudePath
import remocra.db.jooq.remocra.tables.Diametre.DiametrePath
import remocra.db.jooq.remocra.tables.NatureDeci.NatureDeciPath
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
open class PeiProjet(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, Record>?,
    parentPath: InverseForeignKey<out Record, Record>?,
    aliased: Table<Record>?,
    parameters: Array<Field<*>?>?,
    where: Condition?,
) : TableImpl<Record>(
    alias,
    Couverturehydraulique.COUVERTUREHYDRAULIQUE,
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
         * <code>couverturehydraulique.pei_projet</code>
         */
        val PEI_PROJET: PeiProjet = PeiProjet()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>couverturehydraulique.pei_projet.pei_projet_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("pei_projet_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_etude_id</code>.
     */
    val ETUDE_ID: TableField<Record, UUID?> = createField(DSL.name("pei_projet_etude_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_nature_deci_id</code>.
     */
    val NATURE_DECI_ID: TableField<Record, UUID?> = createField(DSL.name("pei_projet_nature_deci_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_type_pei_projet</code>.
     */
    val TYPE_PEI_PROJET: TableField<Record, TypePeiProjet?> = createField(DSL.name("pei_projet_type_pei_projet"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypePeiProjet::class.java), this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_diametre_id</code>.
     */
    val DIAMETRE_ID: TableField<Record, UUID?> = createField(DSL.name("pei_projet_diametre_id"), SQLDataType.UUID, this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_diametre_canalisation</code>.
     */
    val DIAMETRE_CANALISATION: TableField<Record, Int?> = createField(DSL.name("pei_projet_diametre_canalisation"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_capacite</code>.
     */
    val CAPACITE: TableField<Record, Int?> = createField(DSL.name("pei_projet_capacite"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_debit</code>.
     */
    val DEBIT: TableField<Record, Int?> = createField(DSL.name("pei_projet_debit"), SQLDataType.INTEGER, this, "")

    /**
     * The column
     * <code>couverturehydraulique.pei_projet.pei_projet_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("pei_projet_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "", GeometryBinding())

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>couverturehydraulique.pei_projet</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>couverturehydraulique.pei_projet</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>couverturehydraulique.pei_projet</code> table reference
     */
    constructor() : this(DSL.name("pei_projet"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, PEI_PROJET, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class PeiProjetPath : PeiProjet, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): PeiProjetPath = PeiProjetPath(DSL.name(alias), this)
        override fun `as`(alias: Name): PeiProjetPath = PeiProjetPath(alias, this)
        override fun `as`(alias: Table<*>): PeiProjetPath = PeiProjetPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Couverturehydraulique.COUVERTUREHYDRAULIQUE
    override fun getIndexes(): List<Index> = listOf(PEI_PROJET_GEOMETRIE_IDX)
    override fun getPrimaryKey(): UniqueKey<Record> = PEI_PROJET_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(PEI_PROJET__PEI_PROJET_PEI_PROJET_DIAMETRE_ID_FKEY, PEI_PROJET__PEI_PROJET_PEI_PROJET_ETUDE_ID_FKEY, PEI_PROJET__PEI_PROJET_PEI_PROJET_NATURE_DECI_ID_FKEY)

    private lateinit var _diametre: DiametrePath

    /**
     * Get the implicit join path to the <code>remocra.diametre</code> table.
     */
    fun diametre(): DiametrePath {
        if (!this::_diametre.isInitialized) {
            _diametre = DiametrePath(this, PEI_PROJET__PEI_PROJET_PEI_PROJET_DIAMETRE_ID_FKEY, null)
        }

        return _diametre
    }

    val diametre: DiametrePath
        get(): DiametrePath = diametre()

    private lateinit var _etude: EtudePath

    /**
     * Get the implicit join path to the
     * <code>couverturehydraulique.etude</code> table.
     */
    fun etude(): EtudePath {
        if (!this::_etude.isInitialized) {
            _etude = EtudePath(this, PEI_PROJET__PEI_PROJET_PEI_PROJET_ETUDE_ID_FKEY, null)
        }

        return _etude
    }

    val etude: EtudePath
        get(): EtudePath = etude()

    private lateinit var _natureDeci: NatureDeciPath

    /**
     * Get the implicit join path to the <code>remocra.nature_deci</code> table.
     */
    fun natureDeci(): NatureDeciPath {
        if (!this::_natureDeci.isInitialized) {
            _natureDeci = NatureDeciPath(this, PEI_PROJET__PEI_PROJET_PEI_PROJET_NATURE_DECI_ID_FKEY, null)
        }

        return _natureDeci
    }

    val natureDeci: NatureDeciPath
        get(): NatureDeciPath = natureDeci()
    override fun getChecks(): List<Check<Record>> = listOf(
        Internal.createCheck(this, DSL.name("geometrie_point_pei_projet"), "((geometrytype(pei_projet_geometrie) = 'POINT'::text))", true),
    )
    override fun `as`(alias: String): PeiProjet = PeiProjet(DSL.name(alias), this)
    override fun `as`(alias: Name): PeiProjet = PeiProjet(alias, this)
    override fun `as`(alias: Table<*>): PeiProjet = PeiProjet(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): PeiProjet = PeiProjet(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): PeiProjet = PeiProjet(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): PeiProjet = PeiProjet(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): PeiProjet = PeiProjet(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): PeiProjet = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): PeiProjet = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): PeiProjet = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): PeiProjet = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): PeiProjet = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): PeiProjet = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): PeiProjet = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): PeiProjet = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): PeiProjet = where(DSL.notExists(select))
}
