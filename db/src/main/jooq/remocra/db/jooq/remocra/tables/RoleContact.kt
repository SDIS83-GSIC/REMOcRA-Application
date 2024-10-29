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
import remocra.db.jooq.remocra.keys.L_CONTACT_ROLE__L_CONTACT_ROLE_ROLE_ID_FKEY
import remocra.db.jooq.remocra.keys.ROLE_PKEY
import remocra.db.jooq.remocra.keys.ROLE_ROLE_CODE_KEY
import remocra.db.jooq.remocra.tables.Contact.ContactPath
import remocra.db.jooq.remocra.tables.LContactRole.LContactRolePath
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
open class RoleContact(
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
         * The reference instance of <code>remocra.role_contact</code>
         */
        val ROLE_CONTACT: RoleContact = RoleContact()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.role_contact.role_contact_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("role_contact_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.role_contact.role_contact_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("role_contact_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.role_contact.role_contact_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("role_contact_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.role_contact.role_contact_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("role_contact_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.role_contact.role_contact_protected</code>.
     */
    val PROTECTED: TableField<Record, Boolean?> = createField(DSL.name("role_contact_protected"), SQLDataType.BOOLEAN, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.role_contact</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.role_contact</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.role_contact</code> table reference
     */
    constructor() : this(DSL.name("role_contact"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, ROLE_CONTACT, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class RoleContactPath : RoleContact, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): RoleContactPath = RoleContactPath(DSL.name(alias), this)
        override fun `as`(alias: Name): RoleContactPath = RoleContactPath(alias, this)
        override fun `as`(alias: Table<*>): RoleContactPath = RoleContactPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = ROLE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(ROLE_ROLE_CODE_KEY)

    private lateinit var _lContactRole: LContactRolePath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_contact_role</code> table
     */
    fun lContactRole(): LContactRolePath {
        if (!this::_lContactRole.isInitialized) {
            _lContactRole = LContactRolePath(this, null, L_CONTACT_ROLE__L_CONTACT_ROLE_ROLE_ID_FKEY.inverseKey)
        }

        return _lContactRole
    }

    val lContactRole: LContactRolePath
        get(): LContactRolePath = lContactRole()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.contact</code> table
     */
    val contact: ContactPath
        get(): ContactPath = lContactRole().contact()
    override fun `as`(alias: String): RoleContact = RoleContact(DSL.name(alias), this)
    override fun `as`(alias: Name): RoleContact = RoleContact(alias, this)
    override fun `as`(alias: Table<*>): RoleContact = RoleContact(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): RoleContact = RoleContact(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): RoleContact = RoleContact(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): RoleContact = RoleContact(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): RoleContact = RoleContact(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): RoleContact = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): RoleContact = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): RoleContact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): RoleContact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): RoleContact = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): RoleContact = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): RoleContact = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): RoleContact = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): RoleContact = where(DSL.notExists(select))
}
