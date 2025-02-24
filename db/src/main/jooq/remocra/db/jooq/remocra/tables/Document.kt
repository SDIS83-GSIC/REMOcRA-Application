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
import remocra.db.jooq.couverturehydraulique.keys.L_ETUDE_DOCUMENT__L_ETUDE_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.couverturehydraulique.tables.Etude.EtudePath
import remocra.db.jooq.couverturehydraulique.tables.LEtudeDocument.LEtudeDocumentPath
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.keys.COURRIER__COURRIER_COURRIER_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_MESURE_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.DOCUMENT_HABILITABLE__DOCUMENT_HABILITABLE_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.DOCUMENT_PKEY
import remocra.db.jooq.remocra.keys.L_ADRESSE_DOCUMENT__L_ADRESSE_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_EVENEMENT_DOCUMENT__L_EVENEMENT_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_MODELE_COURRIER_DOCUMENT__L_MODELE_COURRIER_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PEI_DOCUMENT__L_PEI_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PERMIS_DOCUMENT__L_PERMIS_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.OLDEB_VISITE_DOCUMENT__OLDEB_VISITE_DOCUMENT_OLDEB_VISITE_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.RCCI_DOCUMENT__RCCI_DOCUMENT_RCCI_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.tables.Adresse.AdressePath
import remocra.db.jooq.remocra.tables.Courrier.CourrierPath
import remocra.db.jooq.remocra.tables.DebitSimultaneMesure.DebitSimultaneMesurePath
import remocra.db.jooq.remocra.tables.DocumentHabilitable.DocumentHabilitablePath
import remocra.db.jooq.remocra.tables.Evenement.EvenementPath
import remocra.db.jooq.remocra.tables.LAdresseDocument.LAdresseDocumentPath
import remocra.db.jooq.remocra.tables.LEvenementDocument.LEvenementDocumentPath
import remocra.db.jooq.remocra.tables.LModeleCourrierDocument.LModeleCourrierDocumentPath
import remocra.db.jooq.remocra.tables.LPeiDocument.LPeiDocumentPath
import remocra.db.jooq.remocra.tables.LPermisDocument.LPermisDocumentPath
import remocra.db.jooq.remocra.tables.ModeleCourrier.ModeleCourrierPath
import remocra.db.jooq.remocra.tables.OldebVisiteDocument.OldebVisiteDocumentPath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
import remocra.db.jooq.remocra.tables.Permis.PermisPath
import remocra.db.jooq.remocra.tables.RcciDocument.RcciDocumentPath
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
open class Document(
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
         * The reference instance of <code>remocra.document</code>
         */
        val DOCUMENT: Document = Document()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.document.document_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("document_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.document.document_date</code>. Date d'ajout du
     * document dans l'application
     */
    val DATE: TableField<Record, ZonedDateTime?> = createField(DSL.name("document_date"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "Date d'ajout du document dans l'application", ZonedDateTimeBinding())

    /**
     * The column <code>remocra.document.document_nom_fichier</code>.
     */
    val NOM_FICHIER: TableField<Record, String?> = createField(DSL.name("document_nom_fichier"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.document.document_repertoire</code>.
     */
    val REPERTOIRE: TableField<Record, String?> = createField(DSL.name("document_repertoire"), SQLDataType.CLOB.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.document</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.document</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.document</code> table reference
     */
    constructor() : this(DSL.name("document"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, DOCUMENT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class DocumentPath : Document, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): DocumentPath = DocumentPath(DSL.name(alias), this)
        override fun `as`(alias: Name): DocumentPath = DocumentPath(alias, this)
        override fun `as`(alias: Table<*>): DocumentPath = DocumentPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = DOCUMENT_PKEY

    private lateinit var _lEtudeDocument: LEtudeDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>couverturehydraulique.l_etude_document</code> table
     */
    fun lEtudeDocument(): LEtudeDocumentPath {
        if (!this::_lEtudeDocument.isInitialized) {
            _lEtudeDocument = LEtudeDocumentPath(this, null, L_ETUDE_DOCUMENT__L_ETUDE_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lEtudeDocument
    }

    val lEtudeDocument: LEtudeDocumentPath
        get(): LEtudeDocumentPath = lEtudeDocument()

    private lateinit var _courrier: CourrierPath

    /**
     * Get the implicit to-many join path to the <code>remocra.courrier</code>
     * table
     */
    fun courrier(): CourrierPath {
        if (!this::_courrier.isInitialized) {
            _courrier = CourrierPath(this, null, COURRIER__COURRIER_COURRIER_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _courrier
    }

    val courrier: CourrierPath
        get(): CourrierPath = courrier()

    private lateinit var _debitSimultaneMesure: DebitSimultaneMesurePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.debit_simultane_mesure</code> table
     */
    fun debitSimultaneMesure(): DebitSimultaneMesurePath {
        if (!this::_debitSimultaneMesure.isInitialized) {
            _debitSimultaneMesure = DebitSimultaneMesurePath(this, null, DEBIT_SIMULTANE_MESURE__DEBIT_SIMULTANE_MESURE_DEBIT_SIMULTANE_MESURE_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _debitSimultaneMesure
    }

    val debitSimultaneMesure: DebitSimultaneMesurePath
        get(): DebitSimultaneMesurePath = debitSimultaneMesure()

    private lateinit var _documentHabilitable: DocumentHabilitablePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.document_habilitable</code> table
     */
    fun documentHabilitable(): DocumentHabilitablePath {
        if (!this::_documentHabilitable.isInitialized) {
            _documentHabilitable = DocumentHabilitablePath(this, null, DOCUMENT_HABILITABLE__DOCUMENT_HABILITABLE_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _documentHabilitable
    }

    val documentHabilitable: DocumentHabilitablePath
        get(): DocumentHabilitablePath = documentHabilitable()

    private lateinit var _lAdresseDocument: LAdresseDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_adresse_document</code> table
     */
    fun lAdresseDocument(): LAdresseDocumentPath {
        if (!this::_lAdresseDocument.isInitialized) {
            _lAdresseDocument = LAdresseDocumentPath(this, null, L_ADRESSE_DOCUMENT__L_ADRESSE_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lAdresseDocument
    }

    val lAdresseDocument: LAdresseDocumentPath
        get(): LAdresseDocumentPath = lAdresseDocument()

    private lateinit var _lEvenementDocument: LEvenementDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_evenement_document</code> table
     */
    fun lEvenementDocument(): LEvenementDocumentPath {
        if (!this::_lEvenementDocument.isInitialized) {
            _lEvenementDocument = LEvenementDocumentPath(this, null, L_EVENEMENT_DOCUMENT__L_EVENEMENT_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lEvenementDocument
    }

    val lEvenementDocument: LEvenementDocumentPath
        get(): LEvenementDocumentPath = lEvenementDocument()

    private lateinit var _lModeleCourrierDocument: LModeleCourrierDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_modele_courrier_document</code> table
     */
    fun lModeleCourrierDocument(): LModeleCourrierDocumentPath {
        if (!this::_lModeleCourrierDocument.isInitialized) {
            _lModeleCourrierDocument = LModeleCourrierDocumentPath(this, null, L_MODELE_COURRIER_DOCUMENT__L_MODELE_COURRIER_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lModeleCourrierDocument
    }

    val lModeleCourrierDocument: LModeleCourrierDocumentPath
        get(): LModeleCourrierDocumentPath = lModeleCourrierDocument()

    private lateinit var _lPeiDocument: LPeiDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_pei_document</code> table
     */
    fun lPeiDocument(): LPeiDocumentPath {
        if (!this::_lPeiDocument.isInitialized) {
            _lPeiDocument = LPeiDocumentPath(this, null, L_PEI_DOCUMENT__L_PEI_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lPeiDocument
    }

    val lPeiDocument: LPeiDocumentPath
        get(): LPeiDocumentPath = lPeiDocument()

    private lateinit var _lPermisDocument: LPermisDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_permis_document</code> table
     */
    fun lPermisDocument(): LPermisDocumentPath {
        if (!this::_lPermisDocument.isInitialized) {
            _lPermisDocument = LPermisDocumentPath(this, null, L_PERMIS_DOCUMENT__L_PERMIS_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lPermisDocument
    }

    val lPermisDocument: LPermisDocumentPath
        get(): LPermisDocumentPath = lPermisDocument()

    private lateinit var _oldebVisiteDocument: OldebVisiteDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.oldeb_visite_document</code> table
     */
    fun oldebVisiteDocument(): OldebVisiteDocumentPath {
        if (!this::_oldebVisiteDocument.isInitialized) {
            _oldebVisiteDocument = OldebVisiteDocumentPath(this, null, OLDEB_VISITE_DOCUMENT__OLDEB_VISITE_DOCUMENT_OLDEB_VISITE_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _oldebVisiteDocument
    }

    val oldebVisiteDocument: OldebVisiteDocumentPath
        get(): OldebVisiteDocumentPath = oldebVisiteDocument()

    private lateinit var _rcciDocument: RcciDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.rcci_document</code> table
     */
    fun rcciDocument(): RcciDocumentPath {
        if (!this::_rcciDocument.isInitialized) {
            _rcciDocument = RcciDocumentPath(this, null, RCCI_DOCUMENT__RCCI_DOCUMENT_RCCI_DOCUMENT_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _rcciDocument
    }

    val rcciDocument: RcciDocumentPath
        get(): RcciDocumentPath = rcciDocument()

    /**
     * Get the implicit many-to-many join path to the
     * <code>couverturehydraulique.etude</code> table
     */
    val etude: EtudePath
        get(): EtudePath = lEtudeDocument().etude()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.adresse</code> table
     */
    val adresse: AdressePath
        get(): AdressePath = lAdresseDocument().adresse()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.evenement</code> table
     */
    val evenement: EvenementPath
        get(): EvenementPath = lEvenementDocument().evenement()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.modele_courrier</code> table
     */
    val modeleCourrier: ModeleCourrierPath
        get(): ModeleCourrierPath = lModeleCourrierDocument().modeleCourrier()

    /**
     * Get the implicit many-to-many join path to the <code>remocra.pei</code>
     * table
     */
    val pei: PeiPath
        get(): PeiPath = lPeiDocument().pei()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.permis</code> table
     */
    val permis: PermisPath
        get(): PermisPath = lPermisDocument().permis()
    override fun `as`(alias: String): Document = Document(DSL.name(alias), this)
    override fun `as`(alias: Name): Document = Document(alias, this)
    override fun `as`(alias: Table<*>): Document = Document(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Document = Document(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Document = Document(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Document = Document(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Document = Document(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Document = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Document = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Document = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Document = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Document = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Document = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Document = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Document = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Document = where(DSL.notExists(select))
}
