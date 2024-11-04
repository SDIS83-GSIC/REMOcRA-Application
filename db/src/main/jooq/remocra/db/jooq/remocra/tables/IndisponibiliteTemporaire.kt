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
import remocra.db.jooq.remocra.keys.INDISPONIBILITE_TEMPORAIRE_PKEY
import remocra.db.jooq.remocra.keys.L_INDISPONIBILITE_TEMPORAIRE_PEI__L_INDISPONIBILITE_TEMPORAIRE__INDISPONIBILITE_TEMPORAIRE_I_FKEY
import remocra.db.jooq.remocra.tables.LIndisponibiliteTemporairePei.LIndisponibiliteTemporairePeiPath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
import java.time.ZonedDateTime
import java.util.UUID
import javax.annotation.processing.Generated
import kotlin.collections.Collection

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
open class IndisponibiliteTemporaire(
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
         * <code>remocra.indisponibilite_temporaire</code>
         */
        val INDISPONIBILITE_TEMPORAIRE: IndisponibiliteTemporaire = IndisponibiliteTemporaire()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("indisponibilite_temporaire_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_date_debut</code>.
     */
    val DATE_DEBUT: TableField<Record, ZonedDateTime?> = createField(DSL.name("indisponibilite_temporaire_date_debut"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_date_fin</code>.
     */
    val DATE_FIN: TableField<Record, ZonedDateTime?> = createField(DSL.name("indisponibilite_temporaire_date_fin"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_motif</code>.
     */
    val MOTIF: TableField<Record, String?> = createField(DSL.name("indisponibilite_temporaire_motif"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_observation</code>.
     */
    val OBSERVATION: TableField<Record, String?> = createField(DSL.name("indisponibilite_temporaire_observation"), SQLDataType.CLOB, this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_auto_indisponible</code>.
     */
    val BASCULE_AUTO_INDISPONIBLE: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_bascule_auto_indisponible"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_auto_disponible</code>.
     */
    val BASCULE_AUTO_DISPONIBLE: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_bascule_auto_disponible"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_mail_avant_indisponibilite</code>.
     */
    val MAIL_AVANT_INDISPONIBILITE: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_mail_avant_indisponibilite"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_mail_apres_indisponibilite</code>.
     */
    val MAIL_APRES_INDISPONIBILITE: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_mail_apres_indisponibilite"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_debut</code>.
     * Date à laquelle le début de l'indisponibilité temporaire a été notifié
     */
    val NOTIFICATION_DEBUT: TableField<Record, ZonedDateTime?> = createField(DSL.name("indisponibilite_temporaire_notification_debut"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "Date à laquelle le début de l'indisponibilité temporaire a été notifié", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_fin</code>.
     * Date à laquelle la fin de l'indisponibilité temporaire a été notifié
     */
    val NOTIFICATION_FIN: TableField<Record, ZonedDateTime?> = createField(DSL.name("indisponibilite_temporaire_notification_fin"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "Date à laquelle la fin de l'indisponibilité temporaire a été notifié", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_reste_indispo</code>.
     * Date à laquelle les pei restés indispo d'une indisponibilité temporaire
     * ont été notifiés
     */
    val NOTIFICATION_RESTE_INDISPO: TableField<Record, ZonedDateTime?> = createField(DSL.name("indisponibilite_temporaire_notification_reste_indispo"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "Date à laquelle les pei restés indispo d'une indisponibilité temporaire ont été notifiés", ZonedDateTimeBinding())

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_debut</code>.
     * Le calcul_dispo au début de l'indisponibilité temporaire a-t-il déjà été
     * lancé ?
     */
    val BASCULE_DEBUT: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_bascule_debut"), SQLDataType.BOOLEAN.defaultValue(DSL.field(DSL.raw("false"), SQLDataType.BOOLEAN)), this, "Le calcul_dispo au début de l'indisponibilité temporaire a-t-il déjà été lancé ?")

    /**
     * The column
     * <code>remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_fin</code>.
     * Le calcul_dispo à la fin de l'indisponibilité temporaire a-t-il déjà été
     * lancé ?
     */
    val BASCULE_FIN: TableField<Record, Boolean?> = createField(DSL.name("indisponibilite_temporaire_bascule_fin"), SQLDataType.BOOLEAN.defaultValue(DSL.field(DSL.raw("false"), SQLDataType.BOOLEAN)), this, "Le calcul_dispo à la fin de l'indisponibilité temporaire a-t-il déjà été lancé ?")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.indisponibilite_temporaire</code> table
     * reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.indisponibilite_temporaire</code> table
     * reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.indisponibilite_temporaire</code> table reference
     */
    constructor() : this(DSL.name("indisponibilite_temporaire"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, INDISPONIBILITE_TEMPORAIRE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class IndisponibiliteTemporairePath : IndisponibiliteTemporaire, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): IndisponibiliteTemporairePath = IndisponibiliteTemporairePath(DSL.name(alias), this)
        override fun `as`(alias: Name): IndisponibiliteTemporairePath = IndisponibiliteTemporairePath(alias, this)
        override fun `as`(alias: Table<*>): IndisponibiliteTemporairePath = IndisponibiliteTemporairePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = INDISPONIBILITE_TEMPORAIRE_PKEY

    private lateinit var _lIndisponibiliteTemporairePei: LIndisponibiliteTemporairePeiPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_indisponibilite_temporaire_pei</code> table
     */
    fun lIndisponibiliteTemporairePei(): LIndisponibiliteTemporairePeiPath {
        if (!this::_lIndisponibiliteTemporairePei.isInitialized) {
            _lIndisponibiliteTemporairePei = LIndisponibiliteTemporairePeiPath(this, null, L_INDISPONIBILITE_TEMPORAIRE_PEI__L_INDISPONIBILITE_TEMPORAIRE__INDISPONIBILITE_TEMPORAIRE_I_FKEY.inverseKey)
        }

        return _lIndisponibiliteTemporairePei
    }

    val lIndisponibiliteTemporairePei: LIndisponibiliteTemporairePeiPath
        get(): LIndisponibiliteTemporairePeiPath = lIndisponibiliteTemporairePei()

    /**
     * Get the implicit many-to-many join path to the <code>remocra.pei</code>
     * table
     */
    val pei: PeiPath
        get(): PeiPath = lIndisponibiliteTemporairePei().pei()
    override fun `as`(alias: String): IndisponibiliteTemporaire = IndisponibiliteTemporaire(DSL.name(alias), this)
    override fun `as`(alias: Name): IndisponibiliteTemporaire = IndisponibiliteTemporaire(alias, this)
    override fun `as`(alias: Table<*>): IndisponibiliteTemporaire = IndisponibiliteTemporaire(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): IndisponibiliteTemporaire = IndisponibiliteTemporaire(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): IndisponibiliteTemporaire = IndisponibiliteTemporaire(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): IndisponibiliteTemporaire = IndisponibiliteTemporaire(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): IndisponibiliteTemporaire = IndisponibiliteTemporaire(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): IndisponibiliteTemporaire = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): IndisponibiliteTemporaire = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): IndisponibiliteTemporaire = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): IndisponibiliteTemporaire = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): IndisponibiliteTemporaire = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): IndisponibiliteTemporaire = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): IndisponibiliteTemporaire = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): IndisponibiliteTemporaire = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): IndisponibiliteTemporaire = where(DSL.notExists(select))
}
