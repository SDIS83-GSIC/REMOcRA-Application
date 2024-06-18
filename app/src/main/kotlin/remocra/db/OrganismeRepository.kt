package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData.IdLibelleData
import remocra.data.enums.TypeAutoriteDeci
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

    fun getOrganismeForSelect(): List<IdLibelleData> =
        getIdLibelleByCondition(DSL.noCondition())

    fun getAutoriteDeciForSelect(): List<IdLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeAutoriteDeci.entries)))

    fun getServicePublicForSelect(): List<IdLibelleData> =
        getIdLibelleByCondition(DSL.condition(TYPE_ORGANISME.CODE.`in`(TypeServicePublicDeci.entries)))

    private fun getIdLibelleByCondition(condition: Condition): List<IdLibelleData> =
        dsl.select(ORGANISME.ID.`as`("id"), ORGANISME.LIBELLE.`as`("libelle"))
            .from(ORGANISME)
            .join(TYPE_ORGANISME).on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ACTIF)
            .and(condition)
            .orderBy(ORGANISME.LIBELLE.asc())
            .fetchInto()
}
