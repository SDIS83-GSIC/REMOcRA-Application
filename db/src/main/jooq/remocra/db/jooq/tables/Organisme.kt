/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.tables

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
import remocra.db.jooq.Remocra
import remocra.db.jooq.keys.API__API_API_ORGANISME_ID_FKEY
import remocra.db.jooq.keys.ORGANISME_ORGANISME_CODE_KEY
import remocra.db.jooq.keys.ORGANISME_PKEY
import remocra.db.jooq.keys.ORGANISME__ORGANISME_ORGANISME_PARENT_ID_FKEY
import remocra.db.jooq.keys.ORGANISME__ORGANISME_ORGANISME_PROFIL_ORGANISME_ID_FKEY
import remocra.db.jooq.keys.ORGANISME__ORGANISME_ORGANISME_TYPE_ORGANISME_ID_FKEY
import remocra.db.jooq.keys.ORGANISME__ORGANISME_ORGANISME_ZONE_INTEGRATION_ID_FKEY
import remocra.db.jooq.keys.PEI__PEI_PEI_AUTORITE_DECI_ID_FKEY
import remocra.db.jooq.keys.PEI__PEI_PEI_MAINTENANCE_DECI_ID_FKEY
import remocra.db.jooq.keys.PEI__PEI_PEI_SERVICE_PUBLIC_DECI_ID_FKEY
import remocra.db.jooq.keys.PIBI__PIBI_PIBI_SERVICE_EAU_ID_FKEY
import remocra.db.jooq.tables.Api.ApiPath
import remocra.db.jooq.tables.Organisme.OrganismePath
import remocra.db.jooq.tables.Pei.PeiPath
import remocra.db.jooq.tables.Pibi.PibiPath
import remocra.db.jooq.tables.ProfilOrganisme.ProfilOrganismePath
import remocra.db.jooq.tables.TypeOrganisme.TypeOrganismePath
import remocra.db.jooq.tables.ZoneIntegration.ZoneIntegrationPath
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
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
open class Organisme(
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
         * The reference instance of <code>remocra.organisme</code>
         */
        val ORGANISME: Organisme = Organisme()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.organisme.organisme_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("organisme_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_actif</code>.
     */
    val ACTIF: TableField<Record, Boolean?> = createField(DSL.name("organisme_actif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_code</code>.
     */
    val CODE: TableField<Record, String?> = createField(DSL.name("organisme_code"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_email_contact</code>.
     * "adresse mail, si possible générique", permettant de contacter
     * l'organisme
     */
    val EMAIL_CONTACT: TableField<Record, String?> = createField(DSL.name("organisme_email_contact"), SQLDataType.CLOB, this, "\"adresse mail, si possible générique\", permettant de contacter l'organisme")

    /**
     * The column <code>remocra.organisme.organisme_profil_organisme_id</code>.
     */
    val PROFIL_ORGANISME_ID: TableField<Record, UUID?> = createField(DSL.name("organisme_profil_organisme_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_type_organisme_id</code>.
     */
    val TYPE_ORGANISME_ID: TableField<Record, UUID?> = createField(DSL.name("organisme_type_organisme_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_zone_integration_id</code>.
     */
    val ZONE_INTEGRATION_ID: TableField<Record, UUID?> = createField(DSL.name("organisme_zone_integration_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.organisme.organisme_parent_id</code>.
     */
    val PARENT_ID: TableField<Record, UUID?> = createField(DSL.name("organisme_parent_id"), SQLDataType.UUID, this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.organisme</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.organisme</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.organisme</code> table reference
     */
    constructor() : this(DSL.name("organisme"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, ORGANISME, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class OrganismePath : Organisme, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): OrganismePath = OrganismePath(DSL.name(alias), this)
        override fun `as`(alias: Name): OrganismePath = OrganismePath(alias, this)
        override fun `as`(alias: Table<*>): OrganismePath = OrganismePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = ORGANISME_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(ORGANISME_ORGANISME_CODE_KEY)
    override fun getReferences(): List<ForeignKey<Record, *>> = listOf(ORGANISME__ORGANISME_ORGANISME_PROFIL_ORGANISME_ID_FKEY, ORGANISME__ORGANISME_ORGANISME_TYPE_ORGANISME_ID_FKEY, ORGANISME__ORGANISME_ORGANISME_ZONE_INTEGRATION_ID_FKEY, ORGANISME__ORGANISME_ORGANISME_PARENT_ID_FKEY)

    private lateinit var _profilOrganisme: ProfilOrganismePath

    /**
     * Get the implicit join path to the <code>remocra.profil_organisme</code>
     * table.
     */
    fun profilOrganisme(): ProfilOrganismePath {
        if (!this::_profilOrganisme.isInitialized) {
            _profilOrganisme = ProfilOrganismePath(this, ORGANISME__ORGANISME_ORGANISME_PROFIL_ORGANISME_ID_FKEY, null)
        }

        return _profilOrganisme
    }

    val profilOrganisme: ProfilOrganismePath
        get(): ProfilOrganismePath = profilOrganisme()

    private lateinit var _typeOrganisme: TypeOrganismePath

    /**
     * Get the implicit join path to the <code>remocra.type_organisme</code>
     * table.
     */
    fun typeOrganisme(): TypeOrganismePath {
        if (!this::_typeOrganisme.isInitialized) {
            _typeOrganisme = TypeOrganismePath(this, ORGANISME__ORGANISME_ORGANISME_TYPE_ORGANISME_ID_FKEY, null)
        }

        return _typeOrganisme
    }

    val typeOrganisme: TypeOrganismePath
        get(): TypeOrganismePath = typeOrganisme()

    private lateinit var _zoneIntegration: ZoneIntegrationPath

    /**
     * Get the implicit join path to the <code>remocra.zone_integration</code>
     * table.
     */
    fun zoneIntegration(): ZoneIntegrationPath {
        if (!this::_zoneIntegration.isInitialized) {
            _zoneIntegration = ZoneIntegrationPath(this, ORGANISME__ORGANISME_ORGANISME_ZONE_INTEGRATION_ID_FKEY, null)
        }

        return _zoneIntegration
    }

    val zoneIntegration: ZoneIntegrationPath
        get(): ZoneIntegrationPath = zoneIntegration()

    private lateinit var _organisme: OrganismePath

    /**
     * Get the implicit join path to the <code>remocra.organisme</code> table.
     */
    fun organisme(): OrganismePath {
        if (!this::_organisme.isInitialized) {
            _organisme = OrganismePath(this, ORGANISME__ORGANISME_ORGANISME_PARENT_ID_FKEY, null)
        }

        return _organisme
    }

    val organisme: OrganismePath
        get(): OrganismePath = organisme()

    private lateinit var _api: ApiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.api</code> table
     */
    fun api(): ApiPath {
        if (!this::_api.isInitialized) {
            _api = ApiPath(this, null, API__API_API_ORGANISME_ID_FKEY.inverseKey)
        }

        return _api
    }

    val api: ApiPath
        get(): ApiPath = api()

    private lateinit var _peiPeiAutoriteDeciIdFkey: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table,
     * via the <code>pei_pei_autorite_deci_id_fkey</code> key
     */
    fun peiPeiAutoriteDeciIdFkey(): PeiPath {
        if (!this::_peiPeiAutoriteDeciIdFkey.isInitialized) {
            _peiPeiAutoriteDeciIdFkey = PeiPath(this, null, PEI__PEI_PEI_AUTORITE_DECI_ID_FKEY.inverseKey)
        }

        return _peiPeiAutoriteDeciIdFkey
    }

    val peiPeiAutoriteDeciIdFkey: PeiPath
        get(): PeiPath = peiPeiAutoriteDeciIdFkey()

    private lateinit var _peiPeiMaintenanceDeciIdFkey: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table,
     * via the <code>pei_pei_maintenance_deci_id_fkey</code> key
     */
    fun peiPeiMaintenanceDeciIdFkey(): PeiPath {
        if (!this::_peiPeiMaintenanceDeciIdFkey.isInitialized) {
            _peiPeiMaintenanceDeciIdFkey = PeiPath(this, null, PEI__PEI_PEI_MAINTENANCE_DECI_ID_FKEY.inverseKey)
        }

        return _peiPeiMaintenanceDeciIdFkey
    }

    val peiPeiMaintenanceDeciIdFkey: PeiPath
        get(): PeiPath = peiPeiMaintenanceDeciIdFkey()

    private lateinit var _peiPeiServicePublicDeciIdFkey: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table,
     * via the <code>pei_pei_service_public_deci_id_fkey</code> key
     */
    fun peiPeiServicePublicDeciIdFkey(): PeiPath {
        if (!this::_peiPeiServicePublicDeciIdFkey.isInitialized) {
            _peiPeiServicePublicDeciIdFkey = PeiPath(this, null, PEI__PEI_PEI_SERVICE_PUBLIC_DECI_ID_FKEY.inverseKey)
        }

        return _peiPeiServicePublicDeciIdFkey
    }

    val peiPeiServicePublicDeciIdFkey: PeiPath
        get(): PeiPath = peiPeiServicePublicDeciIdFkey()

    private lateinit var _pibi: PibiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pibi</code> table
     */
    fun pibi(): PibiPath {
        if (!this::_pibi.isInitialized) {
            _pibi = PibiPath(this, null, PIBI__PIBI_PIBI_SERVICE_EAU_ID_FKEY.inverseKey)
        }

        return _pibi
    }

    val pibi: PibiPath
        get(): PibiPath = pibi()
    override fun `as`(alias: String): Organisme = Organisme(DSL.name(alias), this)
    override fun `as`(alias: Name): Organisme = Organisme(alias, this)
    override fun `as`(alias: Table<*>): Organisme = Organisme(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Organisme = Organisme(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Organisme = Organisme(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Organisme = Organisme(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Organisme = Organisme(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Organisme = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Organisme = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Organisme = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Organisme = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Organisme = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Organisme = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Organisme = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Organisme = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Organisme = where(DSL.notExists(select))
}
