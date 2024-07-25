package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.data.enums.TypeAutoriteDeci
import remocra.data.enums.TypeMaintenanceDeci
import remocra.data.enums.TypeServicePublicDeci
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_DWithin
import java.util.UUID

class OrganismeRepository @Inject constructor(private val dsl: DSLContext) {

    companion object {
        val conditionAutoriteDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeAutoriteDeci.entries))
        val conditionServiceDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeServicePublicDeci.entries))
        val conditionMaintenanceDeci = DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeMaintenanceDeci.entries))
    }

    fun getAll(codeTypeOrganisme: String?, limit: Int?, offset: Int?): Collection<Organisme> =
        dsl.select(*ORGANISME.fields())
            .from(ORGANISME)
            .innerJoin(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ACTIF.isTrue)
            .and(TYPE_ORGANISME.ACTIF.isTrue)
            .and(codeTypeOrganisme?.let { TYPE_ORGANISME.CODE.eq(codeTypeOrganisme) })
            .limit(limit).offset(offset)
            .fetchInto()

    fun getOrganismeForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(DSL.noCondition())

    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(conditionAutoriteDeci)

    fun getServicePublicForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(conditionServiceDeci)

    fun getMaintenanceDeciForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(conditionMaintenanceDeci)

    fun getServiceEauForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.eq(GlobalConstants.SERVICE_EAUX)))

    private fun getIdLibelleByCondition(condition: Condition): List<IdCodeLibelleData> =
        dsl.select(ORGANISME.ID.`as`("id"), ORGANISME.CODE.`as`("code"), ORGANISME.LIBELLE.`as`("libelle"))
            .from(ORGANISME)
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ACTIF)
            .and(condition)
            .orderBy(ORGANISME.LIBELLE.asc())
            .fetchInto()

    fun getAutoriteDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionAutoriteDeci)

    fun getServicePublicDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionServiceDeci)

    fun getMaintenanceDeciPei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int) =
        getOrganismePei(coordonneeX, coordonneeY, srid, toleranceCommuneMetres, conditionMaintenanceDeci)

    private fun getOrganismePei(coordonneeX: String, coordonneeY: String, srid: Int, toleranceCommuneMetres: Int, condition: Condition): List<OrganismePei> =
        dsl.select(ORGANISME.ID.`as`("id"), ORGANISME.CODE.`as`("code"), ORGANISME.LIBELLE.`as`("libelle"), TYPE_ORGANISME.CODE.`as`("codeTypeOrganisme"))
            .from(ORGANISME)
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .join(ZONE_INTEGRATION).on(ZONE_INTEGRATION.ID.eq(ORGANISME.ZONE_INTEGRATION_ID))
            .where(ORGANISME.ACTIF)
            .and(condition)
            .ST_DWithin(ZONE_INTEGRATION.GEOMETRIE, srid, coordonneeX.toDouble(), coordonneeY.toDouble(), toleranceCommuneMetres)
            .orderBy(ORGANISME.LIBELLE.asc())
            .fetchInto()

    data class OrganismePei(
        val id: UUID,
        val code: String,
        var libelle: String,
        val codeTypeOrganisme: String,
    )
}
