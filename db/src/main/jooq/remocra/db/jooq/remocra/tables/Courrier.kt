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
import remocra.db.jooq.remocra.enums.TypeDestinataire
import remocra.db.jooq.remocra.keys.COURRIER_PKEY
import remocra.db.jooq.remocra.keys.COURRIER__COURRIER_COURRIER_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.COURRIER__COURRIER_COURRIER_EXPEDITEUR_FKEY
import remocra.db.jooq.remocra.keys.L_COURRIER_UTILISATEUR__L_COURRIER_UTILISATEUR_COURRIER_ID_FKEY
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_COURRIER__L_THEMATIQUE_COURRIER_COURRIER_ID_FKEY
import remocra.db.jooq.remocra.tables.Document.DocumentPath
import remocra.db.jooq.remocra.tables.LCourrierUtilisateur.LCourrierUtilisateurPath
import remocra.db.jooq.remocra.tables.LThematiqueCourrier.LThematiqueCourrierPath
import remocra.db.jooq.remocra.tables.Organisme.OrganismePath
import remocra.db.jooq.remocra.tables.Thematique.ThematiquePath
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
open class Courrier(
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
         * The reference instance of <code>remocra.courrier</code>
         */
        val COURRIER: Courrier = Courrier()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.courrier.courrier_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("courrier_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.courrier.courrier_document_id</code>.
     */
    val DOCUMENT_ID: TableField<Record, UUID?> = createField(DSL.name("courrier_document_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.courrier.courrier_reference</code>.
     */
    val REFERENCE: TableField<Record, String?> = createField(DSL.name("courrier_reference"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.courrier.courrier_type_destinataire</code>.
     */
    val TYPE_DESTINATAIRE: TableField<Record, TypeDestinataire?> = createField(DSL.name("courrier_type_destinataire"), SQLDataType.VARCHAR.asEnumDataType(TypeDestinataire::class.java), this, "")

    /**
     * The column <code>remocra.courrier.courrier_objet</code>.
     */
    val OBJET: TableField<Record, String?> = createField(DSL.name("courrier_objet"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.courrier.courrier_expediteur</code>. Nom de
     * l'organisme de l'utilisateur connecté (qui envoie le courrier)
     */
    val EXPEDITEUR: TableField<Record, UUID?> = createField(DSL.name("courrier_expediteur"), SQLDataType.UUID, this, "Nom de l'organisme de l'utilisateur connecté (qui envoie le courrier)")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.courrier</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.courrier</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.courrier</code> table reference
     */
    constructor() : this(DSL.name("courrier"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, COURRIER, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class CourrierPath : Courrier, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): CourrierPath = CourrierPath(DSL.name(alias), this)
        override fun `as`(alias: Name): CourrierPath = CourrierPath(alias, this)
        override fun `as`(alias: Table<*>): CourrierPath = CourrierPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = COURRIER_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(COURRIER__COURRIER_COURRIER_DOCUMENT_ID_FKEY, COURRIER__COURRIER_COURRIER_EXPEDITEUR_FKEY)

    private lateinit var _document: DocumentPath

    /**
     * Get the implicit join path to the <code>remocra.document</code> table.
     */
    fun document(): DocumentPath {
        if (!this::_document.isInitialized) {
            _document = DocumentPath(this, COURRIER__COURRIER_COURRIER_DOCUMENT_ID_FKEY, null)
        }

        return _document
    }

    val document: DocumentPath
        get(): DocumentPath = document()

    private lateinit var _organisme: OrganismePath

    /**
     * Get the implicit join path to the <code>remocra.organisme</code> table.
     */
    fun organisme(): OrganismePath {
        if (!this::_organisme.isInitialized) {
            _organisme = OrganismePath(this, COURRIER__COURRIER_COURRIER_EXPEDITEUR_FKEY, null)
        }

        return _organisme
    }

    val organisme: OrganismePath
        get(): OrganismePath = organisme()

    private lateinit var _lCourrierUtilisateur: LCourrierUtilisateurPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_courrier_utilisateur</code> table
     */
    fun lCourrierUtilisateur(): LCourrierUtilisateurPath {
        if (!this::_lCourrierUtilisateur.isInitialized) {
            _lCourrierUtilisateur = LCourrierUtilisateurPath(this, null, L_COURRIER_UTILISATEUR__L_COURRIER_UTILISATEUR_COURRIER_ID_FKEY.inverseKey)
        }

        return _lCourrierUtilisateur
    }

    val lCourrierUtilisateur: LCourrierUtilisateurPath
        get(): LCourrierUtilisateurPath = lCourrierUtilisateur()

    private lateinit var _lThematiqueCourrier: LThematiqueCourrierPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_courrier</code> table
     */
    fun lThematiqueCourrier(): LThematiqueCourrierPath {
        if (!this::_lThematiqueCourrier.isInitialized) {
            _lThematiqueCourrier = LThematiqueCourrierPath(this, null, L_THEMATIQUE_COURRIER__L_THEMATIQUE_COURRIER_COURRIER_ID_FKEY.inverseKey)
        }

        return _lThematiqueCourrier
    }

    val lThematiqueCourrier: LThematiqueCourrierPath
        get(): LThematiqueCourrierPath = lThematiqueCourrier()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.utilisateur</code> table
     */
    val utilisateur: UtilisateurPath
        get(): UtilisateurPath = lCourrierUtilisateur().utilisateur()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.thematique</code> table
     */
    val thematique: ThematiquePath
        get(): ThematiquePath = lThematiqueCourrier().thematique()
    override fun `as`(alias: String): Courrier = Courrier(DSL.name(alias), this)
    override fun `as`(alias: Name): Courrier = Courrier(alias, this)
    override fun `as`(alias: Table<*>): Courrier = Courrier(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Courrier = Courrier(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Courrier = Courrier(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Courrier = Courrier(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Courrier = Courrier(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Courrier = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Courrier = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Courrier = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Courrier = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Courrier = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Courrier = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Courrier = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Courrier = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Courrier = where(DSL.notExists(select))
}
