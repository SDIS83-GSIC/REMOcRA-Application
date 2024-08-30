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
import remocra.db.jooq.remocra.keys.L_PROFIL_UTILISATEUR_ORGANISME_DROIT__L_PROFIL_UTILISATEUR_ORGANISME_DROIT_PROFIL_UTILISATEUR_ID_FKEY
import remocra.db.jooq.remocra.keys.PROFIL_UTILISATEUR_PKEY
import remocra.db.jooq.remocra.keys.PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_CODE_KEY
import remocra.db.jooq.remocra.keys.PROFIL_UTILISATEUR__PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_TYPE_ORGANISME_ID_FKEY
import remocra.db.jooq.remocra.keys.UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY
import remocra.db.jooq.remocra.tables.LProfilUtilisateurOrganismeDroit.LProfilUtilisateurOrganismeDroitPath
import remocra.db.jooq.remocra.tables.TypeOrganisme.TypeOrganismePath
import remocra.db.jooq.remocra.tables.Utilisateur.UtilisateurPath
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
open class ProfilUtilisateur(
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
         * The reference instance of <code>remocra.profil_utilisateur</code>
         */
        val PROFIL_UTILISATEUR: ProfilUtilisateur = ProfilUtilisateur()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.profil_utilisateur.profil_utilisateur_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("profil_utilisateur_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.profil_utilisateur.profil_utilisateur_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("profil_utilisateur_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.profil_utilisateur.profil_utilisateur_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("profil_utilisateur_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.profil_utilisateur.profil_utilisateur_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("profil_utilisateur_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column
     * <code>remocra.profil_utilisateur.profil_utilisateur_type_organisme_id</code>.
     */
    val TYPE_ORGANISME_ID: TableField<Record, UUID?> = createField(DSL.name("profil_utilisateur_type_organisme_id"), SQLDataType.UUID.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.profil_utilisateur</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.profil_utilisateur</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.profil_utilisateur</code> table reference
     */
    constructor() : this(DSL.name("profil_utilisateur"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, PROFIL_UTILISATEUR, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ProfilUtilisateurPath : ProfilUtilisateur, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ProfilUtilisateurPath = ProfilUtilisateurPath(DSL.name(alias), this)
        override fun `as`(alias: Name): ProfilUtilisateurPath = ProfilUtilisateurPath(alias, this)
        override fun `as`(alias: Table<*>): ProfilUtilisateurPath = ProfilUtilisateurPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = PROFIL_UTILISATEUR_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_CODE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(PROFIL_UTILISATEUR__PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_TYPE_ORGANISME_ID_FKEY)

    private lateinit var _typeOrganisme: TypeOrganismePath

    /**
     * Get the implicit join path to the <code>remocra.type_organisme</code>
     * table.
     */
    fun typeOrganisme(): TypeOrganismePath {
        if (!this::_typeOrganisme.isInitialized) {
            _typeOrganisme = TypeOrganismePath(this, PROFIL_UTILISATEUR__PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_TYPE_ORGANISME_ID_FKEY, null)
        }

        return _typeOrganisme
    }

    val typeOrganisme: TypeOrganismePath
        get(): TypeOrganismePath = typeOrganisme()

    private lateinit var _lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_profil_utilisateur_organisme_droit</code> table
     */
    fun lProfilUtilisateurOrganismeDroit(): LProfilUtilisateurOrganismeDroitPath {
        if (!this::_lProfilUtilisateurOrganismeDroit.isInitialized) {
            _lProfilUtilisateurOrganismeDroit = LProfilUtilisateurOrganismeDroitPath(this, null, L_PROFIL_UTILISATEUR_ORGANISME_DROIT__L_PROFIL_UTILISATEUR_ORGANISME_DROIT_PROFIL_UTILISATEUR_ID_FKEY.inverseKey)
        }

        return _lProfilUtilisateurOrganismeDroit
    }

    val lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeDroitPath
        get(): LProfilUtilisateurOrganismeDroitPath = lProfilUtilisateurOrganismeDroit()

    private lateinit var _utilisateur: UtilisateurPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.utilisateur</code> table
     */
    fun utilisateur(): UtilisateurPath {
        if (!this::_utilisateur.isInitialized) {
            _utilisateur = UtilisateurPath(this, null, UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY.inverseKey)
        }

        return _utilisateur
    }

    val utilisateur: UtilisateurPath
        get(): UtilisateurPath = utilisateur()
    override fun `as`(alias: String): ProfilUtilisateur = ProfilUtilisateur(DSL.name(alias), this)
    override fun `as`(alias: Name): ProfilUtilisateur = ProfilUtilisateur(alias, this)
    override fun `as`(alias: Table<*>): ProfilUtilisateur = ProfilUtilisateur(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): ProfilUtilisateur = ProfilUtilisateur(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): ProfilUtilisateur = ProfilUtilisateur(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): ProfilUtilisateur = ProfilUtilisateur(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): ProfilUtilisateur = ProfilUtilisateur(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): ProfilUtilisateur = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): ProfilUtilisateur = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): ProfilUtilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): ProfilUtilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): ProfilUtilisateur = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): ProfilUtilisateur = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): ProfilUtilisateur = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): ProfilUtilisateur = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): ProfilUtilisateur = where(DSL.notExists(select))
}
