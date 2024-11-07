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
import remocra.db.jooq.remocra.keys.JOB__JOB_JOB_UTILISATEUR_ID_FKEY
import remocra.db.jooq.remocra.keys.TOURNEE__TOURNEE_TOURNEE_RESERVATION_UTILISATEUR_ID_FKEY
import remocra.db.jooq.remocra.keys.UTILISATEUR_PKEY
import remocra.db.jooq.remocra.keys.UTILISATEUR_UTILISATEUR_EMAIL_KEY
import remocra.db.jooq.remocra.keys.UTILISATEUR_UTILISATEUR_USERNAME_KEY
import remocra.db.jooq.remocra.keys.UTILISATEUR__UTILISATEUR_UTILISATEUR_ORGANISME_ID_FKEY
import remocra.db.jooq.remocra.keys.UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY
import remocra.db.jooq.remocra.tables.Job.JobPath
import remocra.db.jooq.remocra.tables.Organisme.OrganismePath
import remocra.db.jooq.remocra.tables.ProfilUtilisateur.ProfilUtilisateurPath
import remocra.db.jooq.remocra.tables.Tournee.TourneePath
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
open class Utilisateur(
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
         * The reference instance of <code>remocra.utilisateur</code>
         */
        val UTILISATEUR: Utilisateur = Utilisateur()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.utilisateur.utilisateur_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("utilisateur_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("utilisateur_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_email</code>.
     */
    val EMAIL: TableField<Record, String?> = createField(DSL.name("utilisateur_email"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_nom</code>.
     */
    val NOM: TableField<Record, String?> = createField(DSL.name("utilisateur_nom"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_prenom</code>.
     */
    val PRENOM: TableField<Record, String?> = createField(DSL.name("utilisateur_prenom"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_username</code>.
     */
    val USERNAME: TableField<Record, String?> = createField(DSL.name("utilisateur_username"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_telephone</code>.
     */
    val TELEPHONE: TableField<Record, String?> = createField(DSL.name("utilisateur_telephone"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_can_be_notified</code>.
     */
    val CAN_BE_NOTIFIED: TableField<Record, Boolean?> = createField(DSL.name("utilisateur_can_be_notified"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field(DSL.raw("true"), SQLDataType.BOOLEAN)), this, "")

    /**
     * The column
     * <code>remocra.utilisateur.utilisateur_profil_utilisateur_id</code>.
     */
    val PROFIL_UTILISATEUR_ID: TableField<Record, UUID?> = createField(DSL.name("utilisateur_profil_utilisateur_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_organisme_id</code>.
     */
    val ORGANISME_ID: TableField<Record, UUID?> = createField(DSL.name("utilisateur_organisme_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>remocra.utilisateur.utilisateur_is_super_admin</code>.
     */
    val IS_SUPER_ADMIN: TableField<Record, Boolean?> = createField(DSL.name("utilisateur_is_super_admin"), SQLDataType.BOOLEAN, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.utilisateur</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.utilisateur</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.utilisateur</code> table reference
     */
    constructor() : this(DSL.name("utilisateur"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, UTILISATEUR, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class UtilisateurPath : Utilisateur, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): UtilisateurPath = UtilisateurPath(DSL.name(alias), this)
        override fun `as`(alias: Name): UtilisateurPath = UtilisateurPath(alias, this)
        override fun `as`(alias: Table<*>): UtilisateurPath = UtilisateurPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = UTILISATEUR_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(UTILISATEUR_UTILISATEUR_EMAIL_KEY, UTILISATEUR_UTILISATEUR_USERNAME_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(UTILISATEUR__UTILISATEUR_UTILISATEUR_ORGANISME_ID_FKEY, UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY)

    private lateinit var _organisme: OrganismePath

    /**
     * Get the implicit join path to the <code>remocra.organisme</code> table.
     */
    fun organisme(): OrganismePath {
        if (!this::_organisme.isInitialized) {
            _organisme = OrganismePath(this, UTILISATEUR__UTILISATEUR_UTILISATEUR_ORGANISME_ID_FKEY, null)
        }

        return _organisme
    }

    val organisme: OrganismePath
        get(): OrganismePath = organisme()

    private lateinit var _profilUtilisateur: ProfilUtilisateurPath

    /**
     * Get the implicit join path to the <code>remocra.profil_utilisateur</code>
     * table.
     */
    fun profilUtilisateur(): ProfilUtilisateurPath {
        if (!this::_profilUtilisateur.isInitialized) {
            _profilUtilisateur = ProfilUtilisateurPath(this, UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY, null)
        }

        return _profilUtilisateur
    }

    val profilUtilisateur: ProfilUtilisateurPath
        get(): ProfilUtilisateurPath = profilUtilisateur()

    private lateinit var _job: JobPath

    /**
     * Get the implicit to-many join path to the <code>remocra.job</code> table
     */
    fun job(): JobPath {
        if (!this::_job.isInitialized) {
            _job = JobPath(this, null, JOB__JOB_JOB_UTILISATEUR_ID_FKEY.inverseKey)
        }

        return _job
    }

    val job: JobPath
        get(): JobPath = job()

    private lateinit var _tournee: TourneePath

    /**
     * Get the implicit to-many join path to the <code>remocra.tournee</code>
     * table
     */
    fun tournee(): TourneePath {
        if (!this::_tournee.isInitialized) {
            _tournee = TourneePath(this, null, TOURNEE__TOURNEE_TOURNEE_RESERVATION_UTILISATEUR_ID_FKEY.inverseKey)
        }

        return _tournee
    }

    val tournee: TourneePath
        get(): TourneePath = tournee()
    override fun `as`(alias: String): Utilisateur = Utilisateur(DSL.name(alias), this)
    override fun `as`(alias: Name): Utilisateur = Utilisateur(alias, this)
    override fun `as`(alias: Table<*>): Utilisateur = Utilisateur(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Utilisateur = Utilisateur(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Utilisateur = Utilisateur(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Utilisateur = Utilisateur(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Utilisateur = Utilisateur(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Utilisateur = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Utilisateur = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Utilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Utilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Utilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Utilisateur = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Utilisateur = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Utilisateur = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Utilisateur = where(DSL.notExists(select))
}
