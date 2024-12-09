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
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.bindings.GeometryBinding
import remocra.db.jooq.couverturehydraulique.keys.L_ETUDE_COMMUNE__L_ETUDE_COMMUNE_COMMUNE_ID_FKEY
import remocra.db.jooq.couverturehydraulique.tables.Etude.EtudePath
import remocra.db.jooq.couverturehydraulique.tables.LEtudeCommune.LEtudeCommunePath
import remocra.db.jooq.incoming.keys.CONTACT__CONTACT_CONTACT_COMMUNE_ID_FKEY
import remocra.db.jooq.incoming.keys.NEW_PEI__NEW_PEI_NEW_PEI_COMMUNE_ID_FKEY
import remocra.db.jooq.incoming.tables.Contact.ContactPath
import remocra.db.jooq.incoming.tables.NewPei.NewPeiPath
import remocra.db.jooq.remocra.Remocra
import remocra.db.jooq.remocra.keys.COMMUNE_COMMUNE_CODE_INSEE_KEY
import remocra.db.jooq.remocra.keys.COMMUNE_PKEY
import remocra.db.jooq.remocra.keys.LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY
import remocra.db.jooq.remocra.keys.L_COMMUNE_CIS__L_COMMUNE_CIS_COMMUNE_ID_FKEY
import remocra.db.jooq.remocra.keys.PEI__PEI_PEI_COMMUNE_ID_FKEY
import remocra.db.jooq.remocra.keys.VOIE__VOIE_VOIE_COMMUNE_ID_FKEY
import remocra.db.jooq.remocra.tables.LCommuneCis.LCommuneCisPath
import remocra.db.jooq.remocra.tables.LieuDit.LieuDitPath
import remocra.db.jooq.remocra.tables.Organisme.OrganismePath
import remocra.db.jooq.remocra.tables.Pei.PeiPath
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
open class Commune(
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
         * The reference instance of <code>remocra.commune</code>
         */
        val COMMUNE: Commune = Commune()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<Record> = Record::class.java

    /**
     * The column <code>remocra.commune.commune_id</code>.
     */
    val ID: TableField<Record, UUID?> = createField(DSL.name("commune_id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>remocra.commune.commune_libelle</code>.
     */
    val LIBELLE: TableField<Record, String?> = createField(DSL.name("commune_libelle"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.commune.commune_code_insee</code>.
     */
    val CODE_INSEE: TableField<Record, String?> = createField(DSL.name("commune_code_insee"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.commune.commune_code_postal</code>.
     */
    val CODE_POSTAL: TableField<Record, String?> = createField(DSL.name("commune_code_postal"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>remocra.commune.commune_geometrie</code>.
     */
    val GEOMETRIE: TableField<Record, Geometry?> = createField(DSL.name("commune_geometrie"), SQLDataType.GEOMETRY.nullable(false), this, "", GeometryBinding())

    /**
     * The column <code>remocra.commune.commune_pprif</code>.
     */
    val PPRIF: TableField<Record, Boolean?> = createField(DSL.name("commune_pprif"), SQLDataType.BOOLEAN.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<Record>?) : this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<Record>?, parameters: Array<Field<*>?>?) : this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<Record>?, where: Condition?) : this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>remocra.commune</code> table reference
     */
    constructor(alias: String) : this(DSL.name(alias))

    /**
     * Create an aliased <code>remocra.commune</code> table reference
     */
    constructor(alias: Name) : this(alias, null)

    /**
     * Create a <code>remocra.commune</code> table reference
     */
    constructor() : this(DSL.name("commune"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, COMMUNE, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class CommunePath : Commune, Path<Record> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, Record>?, parentPath: InverseForeignKey<out Record, Record>?) : super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<Record>) : super(alias, aliased)
        override fun `as`(alias: String): CommunePath = CommunePath(DSL.name(alias), this)
        override fun `as`(alias: Name): CommunePath = CommunePath(alias, this)
        override fun `as`(alias: Table<*>): CommunePath = CommunePath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Remocra.REMOCRA
    override fun getPrimaryKey(): UniqueKey<Record> = COMMUNE_PKEY
    override fun getUniqueKeys(): List<UniqueKey<Record>> = listOf(COMMUNE_COMMUNE_CODE_INSEE_KEY)

    private lateinit var _lEtudeCommune: LEtudeCommunePath

    /**
     * Get the implicit to-many join path to the
     * <code>couverturehydraulique.l_etude_commune</code> table
     */
    fun lEtudeCommune(): LEtudeCommunePath {
        if (!this::_lEtudeCommune.isInitialized) {
            _lEtudeCommune = LEtudeCommunePath(this, null, L_ETUDE_COMMUNE__L_ETUDE_COMMUNE_COMMUNE_ID_FKEY.inverseKey)
        }

        return _lEtudeCommune
    }

    val lEtudeCommune: LEtudeCommunePath
        get(): LEtudeCommunePath = lEtudeCommune()

    private lateinit var _contact: ContactPath

    /**
     * Get the implicit to-many join path to the <code>incoming.contact</code>
     * table
     */
    fun contact(): ContactPath {
        if (!this::_contact.isInitialized) {
            _contact = ContactPath(this, null, CONTACT__CONTACT_CONTACT_COMMUNE_ID_FKEY.inverseKey)
        }

        return _contact
    }

    val contact: ContactPath
        get(): ContactPath = contact()

    private lateinit var _newPei: NewPeiPath

    /**
     * Get the implicit to-many join path to the <code>incoming.new_pei</code>
     * table
     */
    fun newPei(): NewPeiPath {
        if (!this::_newPei.isInitialized) {
            _newPei = NewPeiPath(this, null, NEW_PEI__NEW_PEI_NEW_PEI_COMMUNE_ID_FKEY.inverseKey)
        }

        return _newPei
    }

    val newPei: NewPeiPath
        get(): NewPeiPath = newPei()

    private lateinit var _lCommuneCis: LCommuneCisPath

    /**
     * Get the implicit to-many join path to the
     * <code>remocra.l_commune_cis</code> table
     */
    fun lCommuneCis(): LCommuneCisPath {
        if (!this::_lCommuneCis.isInitialized) {
            _lCommuneCis = LCommuneCisPath(this, null, L_COMMUNE_CIS__L_COMMUNE_CIS_COMMUNE_ID_FKEY.inverseKey)
        }

        return _lCommuneCis
    }

    val lCommuneCis: LCommuneCisPath
        get(): LCommuneCisPath = lCommuneCis()

    private lateinit var _lieuDit: LieuDitPath

    /**
     * Get the implicit to-many join path to the <code>remocra.lieu_dit</code>
     * table
     */
    fun lieuDit(): LieuDitPath {
        if (!this::_lieuDit.isInitialized) {
            _lieuDit = LieuDitPath(this, null, LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY.inverseKey)
        }

        return _lieuDit
    }

    val lieuDit: LieuDitPath
        get(): LieuDitPath = lieuDit()

    private lateinit var _pei: PeiPath

    /**
     * Get the implicit to-many join path to the <code>remocra.pei</code> table
     */
    fun pei(): PeiPath {
        if (!this::_pei.isInitialized) {
            _pei = PeiPath(this, null, PEI__PEI_PEI_COMMUNE_ID_FKEY.inverseKey)
        }

        return _pei
    }

    val pei: PeiPath
        get(): PeiPath = pei()

    private lateinit var _voie: VoiePath

    /**
     * Get the implicit to-many join path to the <code>remocra.voie</code> table
     */
    fun voie(): VoiePath {
        if (!this::_voie.isInitialized) {
            _voie = VoiePath(this, null, VOIE__VOIE_VOIE_COMMUNE_ID_FKEY.inverseKey)
        }

        return _voie
    }

    val voie: VoiePath
        get(): VoiePath = voie()

    /**
     * Get the implicit many-to-many join path to the
     * <code>couverturehydraulique.etude</code> table
     */
    val etude: EtudePath
        get(): EtudePath = lEtudeCommune().etude()

    /**
     * Get the implicit many-to-many join path to the
     * <code>remocra.organisme</code> table
     */
    val organisme: OrganismePath
        get(): OrganismePath = lCommuneCis().organisme()
    override fun `as`(alias: String): Commune = Commune(DSL.name(alias), this)
    override fun `as`(alias: Name): Commune = Commune(alias, this)
    override fun `as`(alias: Table<*>): Commune = Commune(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Commune = Commune(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Commune = Commune(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Commune = Commune(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Commune = Commune(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Commune = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Commune = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Commune = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Commune = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Commune = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Commune = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Commune = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Commune = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Commune = where(DSL.notExists(select))
}
