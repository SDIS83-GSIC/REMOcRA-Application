/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.incoming.tables

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
import remocra.db.jooq.incoming.Incoming
import remocra.db.jooq.incoming.keys.L_VISITE_ANOMALIE__L_VISITE_ANOMALIE_VISITE_ID_FKEY
import remocra.db.jooq.incoming.keys.VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY
import remocra.db.jooq.incoming.keys.VISITE_PKEY
import remocra.db.jooq.incoming.keys.VISITE__VISITE_VISITE_PEI_ID_FKEY
import remocra.db.jooq.incoming.keys.VISITE__VISITE_VISITE_TOURNEE_ID_FKEY
import remocra.db.jooq.incoming.tables.LVisiteAnomalie.LVisiteAnomaliePath
import remocra.db.jooq.incoming.tables.Tournee.TourneePath
import remocra.db.jooq.incoming.tables.VisiteCtrlDebitPression.VisiteCtrlDebitPressionPath
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.Anomalie.AnomaliePath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
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
open class Visite(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, Record>?,
    parentPath: InverseForeignKey<out Record, Record>?,
    aliased: Table<Record>?,
    parameters: Array<Field<*>?>?,
    where: Condition?,
) : TableImpl<Record>(
    alias,
    Incoming.INCOMING,
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
         * The reference instance of <code>incoming.visite</code>
         */
        val VISITE: Visite = Visite()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>incoming.visite.visite_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("visite_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>incoming.visite.visite_pei_id</code>.
     */
    val PEI_ID: TableField<Record, UUID?> = createField(DSL.name("visite_pei_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>incoming.visite.visite_tournee_id</code>.
     */
    val TOURNEE_ID: TableField<Record, UUID?> = createField(DSL.name("visite_tournee_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>incoming.visite.visite_date</code>.
     */
    val DATE: TableField<Record, ZonedDateTime?> = createField(DSL.name("visite_date"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "", ZonedDateTimeBinding())

    /**
     * The column <code>incoming.visite.visite_type_visite</code>.
     */
    val TYPE_VISITE: TableField<Record, TypeVisite?> = createField(DSL.name("visite_type_visite"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(TypeVisite::class.java), this, "")

    /**
     * The column <code>incoming.visite.visite_agent1</code>.
     */
    val AGENT1: TableField<Record, String?> = createField(DSL.name("visite_agent1"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.visite.visite_agent2</code>.
     */
    val AGENT2: TableField<Record, String?> = createField(DSL.name("visite_agent2"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.visite.visite_observation</code>.
     */
    val OBSERVATION: TableField<Record, String?> = createField(DSL.name("visite_observation"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.visite.has_anomalie_changes</code>.
     */
    val HAS_ANOMALIE_CHANGES: TableField<Record, Boolean?> = createField(DSL.name("has_anomalie_changes"), SQLDataType.BOOLEAN, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>incoming.visite</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>incoming.visite</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>incoming.visite</code> table reference
     */
    constructor() : this(DSL.name("visite"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, VISITE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class VisitePath : Visite, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): VisitePath = VisitePath(DSL.name(alias), this)
        override fun `as`(alias: Name): VisitePath = VisitePath(alias, this)
        override fun `as`(alias: Table<*>): VisitePath = VisitePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Incoming.INCOMING
    override fun getPrimaryKey(): UniqueKey<Record> = VISITE_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(VISITE__VISITE_VISITE_PEI_ID_FKEY, VISITE__VISITE_VISITE_TOURNEE_ID_FKEY)

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit join path to the <code>remocra.pei</code> table.
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, VISITE__VISITE_VISITE_PEI_ID_FKEY, null)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()

    private lateinit var _tournee: TourneePath

    /**
     * Get the implicit join path to the <code>incoming.tournee</code> table.
     */
    fun tournee(): TourneePath {
        if (!this::_tournee.isInitialized) {
            _tournee = TourneePath(this, VISITE__VISITE_VISITE_TOURNEE_ID_FKEY, null)
        }

        return _tournee
    }

    val tournee: TourneePath
        get(): TourneePath = tournee()

    private lateinit var _lVisiteAnomalie: LVisiteAnomaliePath

    /**
     * Get the implicit to-many join path to the
     * <code>incoming.l_visite_anomalie</code> table
     */
    fun lVisiteAnomalie(): LVisiteAnomaliePath {
        if (!this::_lVisiteAnomalie.isInitialized) {
            _lVisiteAnomalie = LVisiteAnomaliePath(this, null, L_VISITE_ANOMALIE__L_VISITE_ANOMALIE_VISITE_ID_FKEY.inverseKey)
        }

        return _lVisiteAnomalie
    }

    val lVisiteAnomalie: LVisiteAnomaliePath
        get(): LVisiteAnomaliePath = lVisiteAnomalie()

    private lateinit var _visiteCtrlDebitPression: VisiteCtrlDebitPressionPath

    /**
     * Get the implicit to-many join path to the
     * <code>incoming.visite_ctrl_debit_pression</code> table
     */
    fun visiteCtrlDebitPression(): VisiteCtrlDebitPressionPath {
        if (!this::_visiteCtrlDebitPression.isInitialized) {
            _visiteCtrlDebitPression = VisiteCtrlDebitPressionPath(this, null, VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY.inverseKey)
        }

        return _visiteCtrlDebitPression
    }

    val visiteCtrlDebitPression: VisiteCtrlDebitPressionPath
        get(): VisiteCtrlDebitPressionPath = visiteCtrlDebitPression()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.anomalie</code> table
     */
    val anomalie: AnomaliePath
        get(): AnomaliePath = lVisiteAnomalie().anomalie()
    override fun `as`(alias: String): Visite = Visite(DSL.name(alias), this)
    override fun `as`(alias: Name): Visite = Visite(alias, this)
    override fun `as`(alias: Table<*>): Visite = Visite(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Visite = Visite(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Visite = Visite(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Visite = Visite(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Visite = Visite(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Visite = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Visite = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Visite = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Visite = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Visite = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Visite = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Visite = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Visite = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Visite = where(DSL.notExists(select))
}
