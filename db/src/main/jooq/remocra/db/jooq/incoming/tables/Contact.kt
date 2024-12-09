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
import remocra.db.jooq.incoming.Incoming
import remocra.db.jooq.incoming.keys.CONTACT_PKEY
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_CONTACT_COMMUNE_ID_FKEY
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_CONTACT_FONCTION_CONTACT_ID_FKEY
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_CONTACT_LIEU_DIT_ID_FKEY
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_CONTACT_VOIE_ID_FKEY
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_GESTIONNAIRE_ID_FKEY
import remocra.db.jooq.incoming.keys.L_CONTACT_ROLE__L_CONTACT_ROLE_CONTACT_ID_FKEY
import remocra.db.jooq.incoming.tables.Gestionnaire.GestionnairePath
import remocra.db.jooq.incoming.tables.LContactRole.LContactRolePath
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.tables.Commune.CommunePath
import remocra.db.jooq.remocra.tables.FonctionContact.FonctionContactPath
import remocra.db.jooq.remocra.tables.LieuDit.LieuDitPath
import remocra.db.jooq.remocra.tables.RoleContact.RoleContactPath
import remocra.db.jooq.remocra.tables.Voie.VoiePath
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
open class Contact(
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
         * The reference instance of <code>incoming.contact</code>
         */
        val CONTACT: Contact = Contact()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>incoming.contact.contact_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("contact_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>incoming.contact.gestionnaire_id</code>.
     */
    val GESTIONNAIRE_ID: TableField<Record, UUID?> = createField(DSL.name("gestionnaire_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>incoming.contact.contact_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("contact_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>incoming.contact.contact_civilite</code>.
     */
    val CIVILITE: TableField<Record, TypeCivilite?> = createField(DSL.name("contact_civilite"), SQLDataType.VARCHAR.asEnumDataType(TypeCivilite::class.java), this, "")

    /**
     * The column <code>incoming.contact.contact_fonction_contact_id</code>.
     */
    val FONCTION_CONTACT_ID: TableField<Record, UUID?> = createField(DSL.name("contact_fonction_contact_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>incoming.contact.contact_nom</code>.
     */
    val NOM: TableField<Record, String?> = createField(DSL.name("contact_nom"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_prenom</code>.
     */
    val PRENOM: TableField<Record, String?> = createField(DSL.name("contact_prenom"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_numero_voie</code>.
     */
    val NUMERO_VOIE: TableField<Record, String?> = createField(DSL.name("contact_numero_voie"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_suffixe_voie</code>.
     */
    val SUFFIXE_VOIE: TableField<Record, String?> = createField(DSL.name("contact_suffixe_voie"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_lieu_dit_text</code>.
     */
    val LIEU_DIT_TEXT: TableField<Record, String?> = createField(DSL.name("contact_lieu_dit_text"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_lieu_dit_id</code>.
     */
    val LIEU_DIT_ID: TableField<Record, UUID?> = createField(DSL.name("contact_lieu_dit_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>incoming.contact.contact_voie_text</code>.
     */
    val VOIE_TEXT: TableField<Record, String?> = createField(DSL.name("contact_voie_text"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_voie_id</code>.
     */
    val VOIE_ID: TableField<Record, UUID?> = createField(DSL.name("contact_voie_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>incoming.contact.contact_code_postal</code>.
     */
    val CODE_POSTAL: TableField<Record, String?> = createField(DSL.name("contact_code_postal"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_commune_text</code>.
     */
    val COMMUNE_TEXT: TableField<Record, String?> = createField(DSL.name("contact_commune_text"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_commune_id</code>.
     */
    val COMMUNE_ID: TableField<Record, UUID?> = createField(DSL.name("contact_commune_id"), SQLDataType.UUID, this, "")

    /**
     * The column <code>incoming.contact.contact_pays</code>.
     */
    val PAYS: TableField<Record, String?> = createField(DSL.name("contact_pays"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_telephone</code>.
     */
    val TELEPHONE: TableField<Record, String?> = createField(DSL.name("contact_telephone"), SQLDataType.CLOB, this, "")

    /**
     * The column <code>incoming.contact.contact_email</code>.
     */
    val EMAIL: TableField<Record, String?> = createField(DSL.name("contact_email"), SQLDataType.CLOB, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>incoming.contact</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>incoming.contact</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>incoming.contact</code> table reference
     */
    constructor() : this(DSL.name("contact"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, CONTACT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class ContactPath : Contact, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): ContactPath = ContactPath(DSL.name(alias), this)
        override fun `as`(alias: Name): ContactPath = ContactPath(alias, this)
        override fun `as`(alias: Table<*>): ContactPath = ContactPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Incoming.INCOMING
    override fun getPrimaryKey(): UniqueKey<Record> = CONTACT_PKEY
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(CONTACT__CONTACT_CONTACT_COMMUNE_ID_FKEY, CONTACT__CONTACT_CONTACT_FONCTION_CONTACT_ID_FKEY, CONTACT__CONTACT_CONTACT_LIEU_DIT_ID_FKEY, CONTACT__CONTACT_CONTACT_VOIE_ID_FKEY, CONTACT__CONTACT_GESTIONNAIRE_ID_FKEY)

    private lateinit var _commune: CommunePath

    /**
     * Get the implicit join path to the <code>remocra.commune</code> table.
     */
    fun commune(): CommunePath {
        if (!this::_commune.isInitialized) {
            _commune = CommunePath(this, CONTACT__CONTACT_CONTACT_COMMUNE_ID_FKEY, null)
        }

        return _commune
    }

    val commune: CommunePath
        get(): CommunePath = commune()

    private lateinit var _fonctionContact: FonctionContactPath

    /**
     * Get the implicit join path to the <code>remocra.fonction_contact</code>
     * table.
     */
    fun fonctionContact(): FonctionContactPath {
        if (!this::_fonctionContact.isInitialized) {
            _fonctionContact = FonctionContactPath(this, CONTACT__CONTACT_CONTACT_FONCTION_CONTACT_ID_FKEY, null)
        }

        return _fonctionContact
    }

    val fonctionContact: FonctionContactPath
        get(): FonctionContactPath = fonctionContact()

    private lateinit var _lieuDit: LieuDitPath

    /**
     * Get the implicit join path to the <code>remocra.lieu_dit</code> table.
     */
    fun lieuDit(): LieuDitPath {
        if (!this::_lieuDit.isInitialized) {
            _lieuDit = LieuDitPath(this, CONTACT__CONTACT_CONTACT_LIEU_DIT_ID_FKEY, null)
        }

        return _lieuDit
    }

    val lieuDit: LieuDitPath
        get(): LieuDitPath = lieuDit()

    private lateinit var _voie: VoiePath

    /**
     * Get the implicit join path to the <code>remocra.voie</code> table.
     */
    fun voie(): VoiePath {
        if (!this::_voie.isInitialized) {
            _voie = VoiePath(this, CONTACT__CONTACT_CONTACT_VOIE_ID_FKEY, null)
        }

        return _voie
    }

    val voie: VoiePath
        get(): VoiePath = voie()

    private lateinit var _gestionnaire: GestionnairePath

    /**
     * Get the implicit join path to the <code>incoming.gestionnaire</code>
     * table.
     */
    fun gestionnaire(): GestionnairePath {
        if (!this::_gestionnaire.isInitialized) {
            _gestionnaire = GestionnairePath(this, CONTACT__CONTACT_GESTIONNAIRE_ID_FKEY, null)
        }

        return _gestionnaire
    }

    val gestionnaire: GestionnairePath
        get(): GestionnairePath = gestionnaire()

    private lateinit var _lContactRole: LContactRolePath

    /**
     * Get the implicit to-many join path to the
     * <code>incoming.l_contact_role</code> table
     */
    fun lContactRole(): LContactRolePath {
        if (!this::_lContactRole.isInitialized) {
            _lContactRole = LContactRolePath(this, null, L_CONTACT_ROLE__L_CONTACT_ROLE_CONTACT_ID_FKEY.inverseKey)
        }

        return _lContactRole
    }

    val lContactRole: LContactRolePath
        get(): LContactRolePath = lContactRole()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.role_contact</code> table
     */
    val roleContact: RoleContactPath
        get(): RoleContactPath = lContactRole().roleContact()
    override fun `as`(alias: String): Contact = Contact(DSL.name(alias), this)
    override fun `as`(alias: Name): Contact = Contact(alias, this)
    override fun `as`(alias: Table<*>): Contact = Contact(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Contact = Contact(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Contact = Contact(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Contact = Contact(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Contact = Contact(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Contact = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Contact = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Contact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Contact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Contact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Contact = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Contact = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Contact = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Contact = where(DSL.notExists(select))
}
