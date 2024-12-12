package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.GlobalConstants
import remocra.data.GlobalData
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.data.OrganismeData
import remocra.data.Params
import remocra.data.enums.TypeAutoriteDeci
import remocra.data.enums.TypeMaintenanceDeci
import remocra.data.enums.TypeServicePublicDeci
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.tasks.Destinataire
import remocra.utils.ST_DWithin
import remocra.utils.ST_Within
import java.util.UUID

class OrganismeRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        val conditionAutoriteDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeAutoriteDeci.entries))
        val conditionServiceDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeServicePublicDeci.entries))
        val conditionMaintenanceDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeMaintenanceDeci.entries))
    }

    fun getAll(codeTypeOrganisme: String?, limit: Int?, offset: Int?): Collection<OrganismeComplet> =
        dsl.select(*ORGANISME.fields()).from(ORGANISME).innerJoin(TYPE_ORGANISME)
            .on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID)).where(ORGANISME.ACTIF.isTrue)
            .and(TYPE_ORGANISME.ACTIF.isTrue).and(codeTypeOrganisme?.let { TYPE_ORGANISME.CODE.eq(codeTypeOrganisme) })
            .limit(limit).offset(offset).fetchInto()

    fun getAll(): Collection<GlobalData.IdCodeLibelleLienData> =
        dsl.select(
            ORGANISME.ID.`as`("id"),
            ORGANISME.CODE.`as`("code"),
            ORGANISME.LIBELLE.`as`("libelle"),
            ORGANISME.PROFIL_ORGANISME_ID.`as`("lienId"),
        )
            .from(ORGANISME)
            .where(ORGANISME.ACTIF.isTrue)
            .fetchInto()

    fun getOrganismeForSelect(): List<IdCodeLibelleData> = getIdLibelleByCondition(DSL.noCondition())

    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> = getIdLibelleByCondition(conditionAutoriteDeci)

    fun getServicePublicForSelect(): List<IdCodeLibelleData> = getIdLibelleByCondition(conditionServiceDeci)

    fun getMaintenanceDeciForSelect(): List<IdCodeLibelleData> = getIdLibelleByCondition(conditionMaintenanceDeci)

    fun getServiceEauForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.eq(GlobalConstants.SERVICE_EAUX)))

    private fun getIdLibelleByCondition(condition: Condition): List<IdCodeLibelleData> =
        dsl.select(ORGANISME.ID.`as`("id"), ORGANISME.CODE.`as`("code"), ORGANISME.LIBELLE.`as`("libelle"))
            .from(ORGANISME).join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ACTIF).and(condition).orderBy(ORGANISME.LIBELLE.asc()).fetchInto()

    fun getAutoriteDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionAutoriteDeci)

    fun getServicePublicDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionServiceDeci)

    fun getMaintenanceDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionMaintenanceDeci)

    private fun getOrganismePei(
        coordonneeX: String,
        coordonneeY: String,
        srid: Int,
        toleranceCommuneMetres: Int,
        condition: Condition,
    ): List<OrganismePei> = dsl.select(
        ORGANISME.ID.`as`("id"),
        ORGANISME.CODE.`as`("code"),
        ORGANISME.LIBELLE.`as`("libelle"),
        TYPE_ORGANISME.CODE.`as`("codeTypeOrganisme"),
    ).from(ORGANISME).join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID)).join(ZONE_INTEGRATION)
        .on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID)).where(ORGANISME.ACTIF).and(condition).ST_DWithin(
            ZONE_INTEGRATION.GEOMETRIE,
            srid,
            coordonneeX.toDouble(),
            coordonneeY.toDouble(),
            toleranceCommuneMetres,
        ).orderBy(ORGANISME.LIBELLE.asc()).fetchInto()

    data class OrganismePei(
        val id: UUID,
        val code: String,
        var libelle: String,
        val codeTypeOrganisme: String,
    )

    /**
     * Retourne l'ID de l'organisme et de tous ses enfants, pour simplifier les requêtes hiérarchiques
     *
     */
    fun getOrganismeAndChildren(organismeId: UUID): Collection<UUID> {
        val nomCte = name("org")
        val cte = nomCte.fields("organisme_id").`as`(
            select(ORGANISME.ID).from(ORGANISME).where(ORGANISME.ID.eq(organismeId)).unionAll(
                select(ORGANISME.ID).from(table(nomCte)).innerJoin(ORGANISME)
                    .on(ORGANISME.PARENT_ID.eq(field(name("org", "organisme_id"), SQLDataType.UUID))),
            ),
        )
        return dsl.withRecursive(cte).selectFrom(cte).fetchInto()
    }

    data class Filter(
        val organismeActif: Boolean?,
        val organismeCode: String?,
        val organismeLibelle: String?,
        val organismeEmailContact: String?,
        val typeOrganismeLibelle: String?,
        val profilOrganismeLibelle: String?,
        val zoneIntegrationLibelle: String?,
        val parentLibelle: String?,
    ) {
        fun toCondition(): Condition {
            val parent = ORGANISME.`as`("parent")
            return DSL.and(
                listOfNotNull(
                    organismeActif?.let { DSL.and(ORGANISME.ACTIF.eq(organismeActif)) },
                    organismeCode?.let { DSL.and(ORGANISME.CODE.contains(organismeCode)) },
                    organismeLibelle?.let { DSL.and(ORGANISME.LIBELLE.contains(organismeLibelle)) },
                    organismeEmailContact?.let { DSL.and(ORGANISME.EMAIL_CONTACT.contains(organismeEmailContact)) },
                    typeOrganismeLibelle?.let { DSL.and(TYPE_ORGANISME.LIBELLE.contains(typeOrganismeLibelle)) },
                    profilOrganismeLibelle?.let { DSL.and(PROFIL_ORGANISME.LIBELLE.contains(profilOrganismeLibelle)) },
                    zoneIntegrationLibelle?.let { DSL.and(ZONE_INTEGRATION.LIBELLE.contains(zoneIntegrationLibelle)) },
                    parentLibelle?.let { DSL.and(parent.LIBELLE.contains(parentLibelle)) },
                ),
            )
        }
    }

    data class Sort(
        val organismeActif: Int?,
        val organismeCode: Int?,
        val organismeLibelle: Int?,
        val organismeEmailContact: Int?,
        val typeOrganismeLibelle: Int?,
        val profilOrganismeLibelle: Int?,
        val zoneIntegrationLibelle: Int?,
        val parentLibelle: Int?,
    ) {
        fun toCondition(): List<SortField<*>> {
            val parent = ORGANISME.`as`("parent")
            return listOfNotNull(
                ORGANISME.ACTIF.getSortField(organismeActif),
                ORGANISME.CODE.getSortField(organismeCode),
                ORGANISME.LIBELLE.getSortField(organismeLibelle),
                ORGANISME.EMAIL_CONTACT.getSortField(organismeEmailContact),
                TYPE_ORGANISME.LIBELLE.getSortField(typeOrganismeLibelle),
                PROFIL_ORGANISME.LIBELLE.getSortField(profilOrganismeLibelle),
                ZONE_INTEGRATION.LIBELLE.getSortField(zoneIntegrationLibelle),
                parent.LIBELLE.getSortField(parentLibelle),
            )
        }
    }

    data class OrganismeComplet(
        val organismeId: UUID,
        val organismeActif: Boolean,
        val organismeCode: String,
        val organismeLibelle: String,
        val organismeEmailContact: String?,
        val typeOrganismeLibelle: String,
        val profilOrganismeLibelle: String,
        val zoneIntegrationLibelle: String?,
        val parentLibelle: String?,
        val hasContact: Boolean,
    )

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<OrganismeComplet> {
        val parent = ORGANISME.`as`("parent")
        return dsl.select(
            ORGANISME.ID,
            ORGANISME.ACTIF,
            ORGANISME.CODE,
            ORGANISME.LIBELLE,
            ORGANISME.EMAIL_CONTACT,
            PROFIL_ORGANISME.LIBELLE,
            ZONE_INTEGRATION.LIBELLE,
            TYPE_ORGANISME.LIBELLE,
            parent.LIBELLE.`as`("parent_libelle"),
            DSL.field(
                DSL.exists(
                    dsl.select(L_CONTACT_ORGANISME.CONTACT_ID)
                        .from(
                            L_CONTACT_ORGANISME,
                        )
                        .where(L_CONTACT_ORGANISME.ORGANISME_ID.eq(ORGANISME.ID)),
                ),
            ).`as`("hasContact"),
        ).from(ORGANISME)
            .leftJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .leftJoin(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .leftJoin(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .leftJoin(parent).on(ORGANISME.PARENT_ID.eq(parent.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition() ?: listOf(ORGANISME.CODE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()
    }

    fun getCountForAdmin(params: Params<Filter, Sort>): Int {
        val parent = ORGANISME.`as`("parent")
        return dsl.selectDistinct(ORGANISME.ID).from(ORGANISME)
            .leftJoin(PROFIL_ORGANISME).on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
            .leftJoin(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .leftJoin(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .leftJoin(parent).on(ORGANISME.PARENT_ID.eq(parent.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition()).count()
    }

    fun add(organismeData: OrganismeData): Int {
        return dsl.insertInto(
            ORGANISME,
            ORGANISME.ID,
            ORGANISME.ACTIF,
            ORGANISME.CODE,
            ORGANISME.LIBELLE,
            ORGANISME.EMAIL_CONTACT,
            ORGANISME.PROFIL_ORGANISME_ID,
            ORGANISME.TYPE_ORGANISME_ID,
            ORGANISME.ZONE_INTEGRATION_ID,
            ORGANISME.PARENT_ID,
        ).values(
            organismeData.organismeId,
            organismeData.organismeActif,
            organismeData.organismeCode,
            organismeData.organismeLibelle,
            organismeData.organismeEmailContact,
            organismeData.organismeProfilOrganismeId,
            organismeData.organismeTypeOrganismeId,
            organismeData.organismeZoneIntegrationId,
            organismeData.organismeParentId,
        ).execute()
    }

    fun getActive(): Collection<Organisme> {
        return dsl.select(*ORGANISME.fields())
            .from(ORGANISME)
            .where(ORGANISME.ACTIF.isTrue)
            .orderBy(ORGANISME.LIBELLE)
            .fetchInto()
    }

    fun edit(organisme: OrganismeData): Int =
        dsl.update(ORGANISME)
            .set(ORGANISME.ACTIF, organisme.organismeActif)
            .set(ORGANISME.CODE, organisme.organismeCode)
            .set(ORGANISME.LIBELLE, organisme.organismeLibelle)
            .set(ORGANISME.EMAIL_CONTACT, organisme.organismeEmailContact)
            .set(ORGANISME.PROFIL_ORGANISME_ID, organisme.organismeProfilOrganismeId)
            .set(ORGANISME.TYPE_ORGANISME_ID, organisme.organismeTypeOrganismeId)
            .set(ORGANISME.ZONE_INTEGRATION_ID, organisme.organismeZoneIntegrationId)
            .set(ORGANISME.PARENT_ID, organisme.organismeParentId)
            .where(ORGANISME.ID.eq(organisme.organismeId))
            .execute()

    fun getById(id: UUID): OrganismeData? =
        dsl.selectFrom(ORGANISME).where(ORGANISME.ID.eq(id)).fetchOneInto()

    fun getDestinataireContactOrganisme(listePeiId: List<UUID>, typeOrganisme: List<UUID>, contactRole: String): Map<Destinataire, List<UUID?>> =
        dsl.select(
            PEI.ID,
            CONTACT.ID,
            CONTACT.CIVILITE,
            FONCTION_CONTACT.LIBELLE,
            CONTACT.NOM,
            CONTACT.PRENOM,
            CONTACT.EMAIL,
        )
            .from(ORGANISME)
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .join(ZONE_INTEGRATION).on(ORGANISME.ZONE_INTEGRATION_ID.eq(ZONE_INTEGRATION.ID))
            .join(PEI).on(ST_Within(PEI.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .join(L_CONTACT_ORGANISME).on(ORGANISME.ID.eq(L_CONTACT_ORGANISME.ORGANISME_ID))
            .join(CONTACT).on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
            .join(L_CONTACT_ROLE).on(CONTACT.ID.eq(L_CONTACT_ROLE.CONTACT_ID))
            .join(ROLE_CONTACT).on(L_CONTACT_ROLE.ROLE_ID.eq(ROLE_CONTACT.ID))
            .leftJoin(FONCTION_CONTACT).on(CONTACT.FONCTION_CONTACT_ID.eq(FONCTION_CONTACT.ID))
            .where(PEI.ID.`in`(listePeiId))
            .and(TYPE_ORGANISME.ID.`in`(typeOrganisme))
            .and(ROLE_CONTACT.CODE.eq(contactRole))
            .and(CONTACT.EMAIL.isNotNull)
            .fetchGroups(
                {
                        record ->
                    Destinataire(
                        destinataireId = record.get(CONTACT.ID),
                        destinataireCivilite = record.get(CONTACT.CIVILITE),
                        destinataireFonction = record.get(FONCTION_CONTACT.LIBELLE),
                        destinataireNom = record.get(CONTACT.NOM),
                        destinatairePrenom = record.get(CONTACT.PRENOM),
                        destinataireEmail = record.get(CONTACT.EMAIL)!!,
                    )
                },
                {
                        record ->
                    record.get(PEI.ID)
                },
            )

    fun getLibelleById(organismeId: UUID): String =
        dsl.select(ORGANISME.LIBELLE)
            .from(ORGANISME)
            .where(ORGANISME.ID.eq(organismeId))
            .fetchSingleInto()
}
