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

class OrganismeRepository @Inject constructor(private val dsl: DSLContext) {
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
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeAutoriteDeci.entries)))

    fun getServicePublicForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeServicePublicDeci.entries)))

    fun getMaintenanceDeciForSelect(): List<IdCodeLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeMaintenanceDeci.entries)))

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
}
