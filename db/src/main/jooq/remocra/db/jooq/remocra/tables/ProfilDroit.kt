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
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.keys.L_COUCHE_DROIT__L_COUCHE_DROIT_PROFIL_DROIT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_MODELE_COURRIER_PROFIL_DROIT__L_MODELE_COURRIER_PROFIL_DROIT_PROFIL_DROIT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PROFIL_DROIT_BLOC_DOCUMENT__L_PROFIL_DROIT_BLOC_DOCUMENT_PROFIL_DROIT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PROFIL_UTILISATEUR_ORGANISME_DROIT__L_PROFIL_UTILISATEUR_ORGANISME_DROIT_PROFIL_DROIT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_RAPPORT_PERSONNALISE_PROFIL_DROIT__L_RAPPORT_PERSONNALISE_PROFIL_DROIT_PROFIL_DROIT_ID_FKEY
import remocra.db.jooq.remocra.keys.PROFIL_DROIT_PKEY
import remocra.db.jooq.remocra.keys.PROFIL_DROIT_PROFIL_DROIT_CODE_KEY
import remocra.db.jooq.remocra.tables.BlocDocument.BlocDocumentPath
import remocra.db.jooq.remocra.tables.Couche.CouchePath
import remocra.db.jooq.remocra.tables.LCoucheDroit.LCoucheDroitPath
import remocra.db.jooq.remocra.tables.LModeleCourrierProfilDroit.LModeleCourrierProfilDroitPath
import remocra.db.jooq.remocra.tables.LProfilDroitBlocDocument.LProfilDroitBlocDocumentPath
import remocra.db.jooq.remocra.tables.LProfilUtilisateurOrganismeDroit.LProfilUtilisateurOrganismeDroitPath
import remocra.db.jooq.remocra.tables.LRapportPersonnaliseProfilDroit.LRapportPersonnaliseProfilDroitPath
import remocra.db.jooq.remocra.tables.ModeleCourrier.ModeleCourrierPath
import remocra.db.jooq.remocra.tables.RapportPersonnalise.RapportPersonnalisePath
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
open class ProfilDroit(
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
         * The reference instance of <code>remocra.profil_droit</code>
         */
        val PROFIL_DROIT: ProfilDroit = ProfilDroit()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.profil_droit.profil_droit_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("profil_droit_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.profil_droit.profil_droit_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("profil_droit_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.profil_droit.profil_droit_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("profil_droit_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.profil_droit.profil_droit_droits</code>.
     */
    val DROITS: TableField<Record, Array<Droit?>?> = createField(DSL.name("profil_droit_droits"), SQLDataType.VARCHAR.nullable(false).asEnumDataType(Droit::class.java).array(), this, "")

    /**
     * The column <code>remocra.profil_droit.profil_droit_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("profil_droit_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.profil_droit</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.profil_droit</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.profil_droit</code> table reference
     */
    constructor() : this(DSL.name("profil_droit"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, PROFIL_DROIT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ProfilDroitPath : ProfilDroit, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ProfilDroitPath = ProfilDroitPath(DSL.name(alias), this)
        override fun `as`(alias: Name): ProfilDroitPath = ProfilDroitPath(alias, this)
        override fun `as`(alias: Table<*>): ProfilDroitPath = ProfilDroitPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = PROFIL_DROIT_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(PROFIL_DROIT_PROFIL_DROIT_CODE_KEY)

    private lateinit var _lCoucheDroit: LCoucheDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_couche_droit</code> table
     */
    fun lCoucheDroit(): LCoucheDroitPath {
        if (!this::_lCoucheDroit.isInitialized) {
            _lCoucheDroit = LCoucheDroitPath(this, null, L_COUCHE_DROIT__L_COUCHE_DROIT_PROFIL_DROIT_ID_FKEY.inverseKey)
        }

        return _lCoucheDroit
    }

    val lCoucheDroit: LCoucheDroitPath
        get(): LCoucheDroitPath = lCoucheDroit()

    private lateinit var _lModeleCourrierProfilDroit: LModeleCourrierProfilDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_modele_courrier_profil_droit</code> table
     */
    fun lModeleCourrierProfilDroit(): LModeleCourrierProfilDroitPath {
        if (!this::_lModeleCourrierProfilDroit.isInitialized) {
            _lModeleCourrierProfilDroit = LModeleCourrierProfilDroitPath(this, null, L_MODELE_COURRIER_PROFIL_DROIT__L_MODELE_COURRIER_PROFIL_DROIT_PROFIL_DROIT_ID_FKEY.inverseKey)
        }

        return _lModeleCourrierProfilDroit
    }

    val lModeleCourrierProfilDroit: LModeleCourrierProfilDroitPath
        get(): LModeleCourrierProfilDroitPath = lModeleCourrierProfilDroit()

    private lateinit var _lProfilDroitBlocDocument: LProfilDroitBlocDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_profil_droit_bloc_document</code> table
     */
    fun lProfilDroitBlocDocument(): LProfilDroitBlocDocumentPath {
        if (!this::_lProfilDroitBlocDocument.isInitialized) {
            _lProfilDroitBlocDocument = LProfilDroitBlocDocumentPath(this, null, L_PROFIL_DROIT_BLOC_DOCUMENT__L_PROFIL_DROIT_BLOC_DOCUMENT_PROFIL_DROIT_ID_FKEY.inverseKey)
        }

        return _lProfilDroitBlocDocument
    }

    val lProfilDroitBlocDocument: LProfilDroitBlocDocumentPath
        get(): LProfilDroitBlocDocumentPath = lProfilDroitBlocDocument()

    private lateinit var _lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_profil_utilisateur_organisme_droit</code> table
     */
    fun lProfilUtilisateurOrganismeDroit(): LProfilUtilisateurOrganismeDroitPath {
        if (!this::_lProfilUtilisateurOrganismeDroit.isInitialized) {
            _lProfilUtilisateurOrganismeDroit = LProfilUtilisateurOrganismeDroitPath(this, null, L_PROFIL_UTILISATEUR_ORGANISME_DROIT__L_PROFIL_UTILISATEUR_ORGANISME_DROIT_PROFIL_DROIT_ID_FKEY.inverseKey)
        }

        return _lProfilUtilisateurOrganismeDroit
    }

    val lProfilUtilisateurOrganismeDroit: LProfilUtilisateurOrganismeDroitPath
        get(): LProfilUtilisateurOrganismeDroitPath = lProfilUtilisateurOrganismeDroit()

    private lateinit var _lRapportPersonnaliseProfilDroit: LRapportPersonnaliseProfilDroitPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_rapport_personnalise_profil_droit</code> table
     */
    fun lRapportPersonnaliseProfilDroit(): LRapportPersonnaliseProfilDroitPath {
        if (!this::_lRapportPersonnaliseProfilDroit.isInitialized) {
            _lRapportPersonnaliseProfilDroit = LRapportPersonnaliseProfilDroitPath(this, null, L_RAPPORT_PERSONNALISE_PROFIL_DROIT__L_RAPPORT_PERSONNALISE_PROFIL_DROIT_PROFIL_DROIT_ID_FKEY.inverseKey)
        }

        return _lRapportPersonnaliseProfilDroit
    }

    val lRapportPersonnaliseProfilDroit: LRapportPersonnaliseProfilDroitPath
        get(): LRapportPersonnaliseProfilDroitPath = lRapportPersonnaliseProfilDroit()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.couche</code> table
     */
    val couche: CouchePath
        get(): CouchePath = lCoucheDroit().couche()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.modele_courrier</code> table
     */
    val modeleCourrier: ModeleCourrierPath
        get(): ModeleCourrierPath = lModeleCourrierProfilDroit().modeleCourrier()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.bloc_document</code> table
     */
    val blocDocument: BlocDocumentPath
        get(): BlocDocumentPath = lProfilDroitBlocDocument().blocDocument()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.rapport_personnalise</code> table
     */
    val rapportPersonnalise: RapportPersonnalisePath
        get(): RapportPersonnalisePath = lRapportPersonnaliseProfilDroit().rapportPersonnalise()
    override fun `as`(alias: String): ProfilDroit = ProfilDroit(DSL.name(alias), this)
    override fun `as`(alias: Name): ProfilDroit = ProfilDroit(alias, this)
    override fun `as`(alias: Table<*>): ProfilDroit = ProfilDroit(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): ProfilDroit = ProfilDroit(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): ProfilDroit = ProfilDroit(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): ProfilDroit = ProfilDroit(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): ProfilDroit = ProfilDroit(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): ProfilDroit = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): ProfilDroit = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): ProfilDroit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): ProfilDroit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): ProfilDroit = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): ProfilDroit = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): ProfilDroit = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): ProfilDroit = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): ProfilDroit = where(DSL.notExists(select))
}
