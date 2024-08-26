/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.couverturehydraulique.tables

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
import remocra.db.jooq.couverturehydraulique.Couverturehydraulique
import remocra.db.jooq.couverturehydraulique.keys.VOIE_LATERALE_PKEY
import remocra.db.jooq.couverturehydraulique.keys.VOIE_LATERALE__VOIE_LATERALE_VOIE_LATERALE_VOIE_VOISINE_FKEY
import remocra.db.jooq.couverturehydraulique.tables.Reseau.ReseauPath
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
open class VoieLaterale(
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
         * <code>couverturehydraulique.voie_laterale</code>
         */
        val VOIE_LATERALE: VoieLaterale = VoieLaterale()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("voie_laterale_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_voie_voisine</code>.
     * Voie voisine de la voie actuelle
     */
    val VOIE_VOISINE: TableField<Record, UUID?> = createField(DSL.name("voie_laterale_voie_voisine"), SQLDataType.UUID.nullable(false), this, "Voie voisine de la voie actuelle")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_angle</code>.
     * Angle que forme la voie avec la voie actuelle
     */
    val ANGLE: TableField<Record, Double?> = createField(DSL.name("voie_laterale_angle"), SQLDataType.DOUBLE, this, "Angle que forme la voie avec la voie actuelle")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_gauche</code>.
     * Indique si la voie est celle se situant le plus à gauche
     */
    val GAUCHE: TableField<Record, Boolean?> = createField(DSL.name("voie_laterale_gauche"), SQLDataType.BOOLEAN, this, "Indique si la voie est celle se situant le plus à gauche")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_droite</code>.
     * Indique si la voie est celle se situant le plus à droite
     */
    val DROITE: TableField<Record, Boolean?> = createField(DSL.name("voie_laterale_droite"), SQLDataType.BOOLEAN, this, "Indique si la voie est celle se situant le plus à droite")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_traversable</code>.
     * Indique si la voie est traversable
     */
    val TRAVERSABLE: TableField<Record, Boolean?> = createField(DSL.name("voie_laterale_traversable"), SQLDataType.BOOLEAN, this, "Indique si la voie est traversable")

    /**
     * The column
     * <code>couverturehydraulique.voie_laterale.voie_laterale_accessible</code>.
     * Voie accessible depuis le point de jonction (non accessible si les voies
     * gauche et droite sont non traversables)
     */
    val ACCESSIBLE: TableField<Record, Boolean?> = createField(DSL.name("voie_laterale_accessible"), SQLDataType.BOOLEAN, this, "Voie accessible depuis le point de jonction (non accessible si les voies gauche et droite sont non traversables)")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>couverturehydraulique.voie_laterale</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>couverturehydraulique.voie_laterale</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>couverturehydraulique.voie_laterale</code> table reference
     */
    constructor() : this(DSL.name("voie_laterale"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, VOIE_LATERALE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class VoieLateralePath : VoieLaterale, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): VoieLateralePath = VoieLateralePath(DSL.name(alias), this)
        override fun `as`(alias: Name): VoieLateralePath = VoieLateralePath(alias, this)
        override fun `as`(alias: Table<*>): VoieLateralePath = VoieLateralePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Couverturehydraulique.COUVERTUREHYDRAULIQUE
    override fun getPrimaryKey(): UniqueKey<Record> = VOIE_LATERALE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(VOIE_LATERALE__VOIE_LATERALE_VOIE_LATERALE_VOIE_VOISINE_FKEY)

    private lateinit var _reseau: ReseauPath

    /**
     * Get the implicit join path to the
     * <code>couverturehydraulique.reseau</code> table.
     */
    fun reseau(): ReseauPath {
        if (!this::_reseau.isInitialized) {
            _reseau = ReseauPath(this, VOIE_LATERALE__VOIE_LATERALE_VOIE_LATERALE_VOIE_VOISINE_FKEY, null)
        }

        return _reseau
    }

    val reseau: ReseauPath
        get(): ReseauPath = reseau()
    override fun `as`(alias: String): VoieLaterale = VoieLaterale(DSL.name(alias), this)
    override fun `as`(alias: Name): VoieLaterale = VoieLaterale(alias, this)
    override fun `as`(alias: Table<*>): VoieLaterale = VoieLaterale(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): VoieLaterale = VoieLaterale(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): VoieLaterale = VoieLaterale(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): VoieLaterale = VoieLaterale(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): VoieLaterale = VoieLaterale(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): VoieLaterale = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): VoieLaterale = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): VoieLaterale = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): VoieLaterale = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): VoieLaterale = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): VoieLaterale = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): VoieLaterale = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): VoieLaterale = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): VoieLaterale = where(DSL.notExists(select))
}
