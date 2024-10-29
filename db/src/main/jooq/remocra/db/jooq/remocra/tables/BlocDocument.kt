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
import remocra.db.jooq.remocra.keys.BLOC_DOCUMENT_PKEY
import remocra.db.jooq.remocra.keys.BLOC_DOCUMENT__BLOC_DOCUMENT_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_PROFIL_DROIT_BLOC_DOCUMENT__L_PROFIL_DROIT_BLOC_DOCUMENT_BLOC_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.keys.L_THEMATIQUE_BLOC_DOCUMENT__L_THEMATIQUE_BLOC_DOCUMENT_BLOC_DOCUMENT_ID_FKEY
import remocra.db.jooq.remocra.tables.Document.DocumentPath
import remocra.db.jooq.remocra.tables.LProfilDroitBlocDocument.LProfilDroitBlocDocumentPath
import remocra.db.jooq.remocra.tables.LThematiqueBlocDocument.LThematiqueBlocDocumentPath
import remocra.db.jooq.remocra.tables.ProfilDroit.ProfilDroitPath
import remocra.db.jooq.remocra.tables.Thematique.ThematiquePath
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
open class BlocDocument(
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
         * The reference instance of <code>remocra.bloc_document</code>
         */
        val BLOC_DOCUMENT: BlocDocument = BlocDocument()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.bloc_document.bloc_document_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("bloc_document_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.bloc_document.document_id</code>.
     */
    val DOCUMENT_ID: TableField<Record, UUID?> = createField(DSL.name("document_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.bloc_document.bloc_document_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("bloc_document_libelle"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.bloc_document.bloc_document_description</code>.
     */
    val DESCRIPTION: TableField<Record, String?> = createField(DSL.name("bloc_document_description"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>remocra.bloc_document.bloc_document_date_maj</code>.
     */
    val DATE_MAJ: TableField<Record, ZonedDateTime?> = createField(DSL.name("bloc_document_date_maj"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "", ZonedDateTimeBinding())

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.bloc_document</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.bloc_document</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.bloc_document</code> table reference
     */
    constructor() : this(DSL.name("bloc_document"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, BLOC_DOCUMENT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class BlocDocumentPath : BlocDocument, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): BlocDocumentPath = BlocDocumentPath(DSL.name(alias), this)
        override fun `as`(alias: Name): BlocDocumentPath = BlocDocumentPath(alias, this)
        override fun `as`(alias: Table<*>): BlocDocumentPath = BlocDocumentPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = BLOC_DOCUMENT_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(BLOC_DOCUMENT__BLOC_DOCUMENT_DOCUMENT_ID_FKEY)

    private lateinit var _document: DocumentPath

    /**
     * Get the implicit join path to the <code>remocra.document</code> table.
     */
    fun document(): DocumentPath {
        if (!this::_document.isInitialized) {
            _document = DocumentPath(this, BLOC_DOCUMENT__BLOC_DOCUMENT_DOCUMENT_ID_FKEY, null)
        }

        return _document
    }

    val document: DocumentPath
        get(): DocumentPath = document()

    private lateinit var _lProfilDroitBlocDocument: LProfilDroitBlocDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_profil_droit_bloc_document</code> table
     */
    fun lProfilDroitBlocDocument(): LProfilDroitBlocDocumentPath {
        if (!this::_lProfilDroitBlocDocument.isInitialized) {
            _lProfilDroitBlocDocument = LProfilDroitBlocDocumentPath(this, null, L_PROFIL_DROIT_BLOC_DOCUMENT__L_PROFIL_DROIT_BLOC_DOCUMENT_BLOC_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lProfilDroitBlocDocument
    }

    val lProfilDroitBlocDocument: LProfilDroitBlocDocumentPath
        get(): LProfilDroitBlocDocumentPath = lProfilDroitBlocDocument()

    private lateinit var _lThematiqueBlocDocument: LThematiqueBlocDocumentPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_thematique_bloc_document</code> table
     */
    fun lThematiqueBlocDocument(): LThematiqueBlocDocumentPath {
        if (!this::_lThematiqueBlocDocument.isInitialized) {
            _lThematiqueBlocDocument = LThematiqueBlocDocumentPath(this, null, L_THEMATIQUE_BLOC_DOCUMENT__L_THEMATIQUE_BLOC_DOCUMENT_BLOC_DOCUMENT_ID_FKEY.inverseKey)
        }

        return _lThematiqueBlocDocument
    }

    val lThematiqueBlocDocument: LThematiqueBlocDocumentPath
        get(): LThematiqueBlocDocumentPath = lThematiqueBlocDocument()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.profil_droit</code> table
     */
    val profilDroit: ProfilDroitPath
        get(): ProfilDroitPath = lProfilDroitBlocDocument().profilDroit()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.thematique</code> table
     */
    val thematique: ThematiquePath
        get(): ThematiquePath = lThematiqueBlocDocument().thematique()
    override fun `as`(alias: String): BlocDocument = BlocDocument(DSL.name(alias), this)
    override fun `as`(alias: Name): BlocDocument = BlocDocument(alias, this)
    override fun `as`(alias: Table<*>): BlocDocument = BlocDocument(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): BlocDocument = BlocDocument(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): BlocDocument = BlocDocument(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): BlocDocument = BlocDocument(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): BlocDocument = BlocDocument(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): BlocDocument = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): BlocDocument = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): BlocDocument = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): BlocDocument = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): BlocDocument = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): BlocDocument = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): BlocDocument = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): BlocDocument = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): BlocDocument = where(DSL.notExists(select))
}
