package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import java.util.UUID

class GestionnaireRepository @Inject constructor(private val dsl: DSLContext) {

    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(GESTIONNAIRE.ID.`as`("id"), GESTIONNAIRE.CODE.`as`("code"), GESTIONNAIRE.LIBELLE.`as`("libelle"))
            .from(GESTIONNAIRE)
            .where(GESTIONNAIRE.ACTIF)
            .fetchInto()

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<Gestionnaire> =
        dsl.select(GESTIONNAIRE.fields().asList())
            .from(GESTIONNAIRE)
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(GESTIONNAIRE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.select(GESTIONNAIRE.ID)
            .from(GESTIONNAIRE)
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    fun getById(gestionnaireId: UUID): Gestionnaire =
        dsl.selectFrom(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(gestionnaireId)).fetchSingleInto()

    data class Filter(
        val gestionnaireCode: String?,
        val gestionnaireLibelle: String?,
        val gestionnaireActif: Boolean?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    gestionnaireCode?.let { DSL.and(GESTIONNAIRE.CODE.contains(it)) },
                    gestionnaireLibelle?.let { DSL.and(GESTIONNAIRE.LIBELLE.contains(it)) },
                    gestionnaireActif?.let { DSL.and(GESTIONNAIRE.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val gestionnaireCode: Int?,
        val gestionnaireLibelle: Int?,
        val gestionnaireActif: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            GESTIONNAIRE.CODE.getSortField(gestionnaireCode),
            GESTIONNAIRE.LIBELLE.getSortField(gestionnaireLibelle),
            GESTIONNAIRE.ACTIF.getSortField(gestionnaireActif),
        )
    }

    fun updateGestionnaire(gestionnaire: Gestionnaire) =
        dsl.update(GESTIONNAIRE)
            .set(GESTIONNAIRE.CODE, gestionnaire.gestionnaireCode)
            .set(GESTIONNAIRE.LIBELLE, gestionnaire.gestionnaireLibelle)
            .set(GESTIONNAIRE.ACTIF, gestionnaire.gestionnaireActif)
            .where(GESTIONNAIRE.ID.eq(gestionnaire.gestionnaireId))
            .execute()

    fun insertGestionnaire(gestionnaire: Gestionnaire) =
        dsl.insertInto(GESTIONNAIRE)
            .set(dsl.newRecord(GESTIONNAIRE, gestionnaire))
            .execute()
}
