package remocra.db

import jakarta.inject.Inject
import jakarta.ws.rs.Produces
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.references.NATURE
import java.util.UUID

@Produces("application/json; charset=UTF-8")
class NatureRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Nature> {

    override fun getMapById(): Map<UUID, Nature> =
        dsl.selectFrom(NATURE).where(NATURE.ACTIF.isTrue).fetchInto<Nature>().associateBy { it.natureId }

/**
     * Retourne l'ensemble des natures
     */
    fun getAllForAdmin(): Collection<Nature> =
        dsl.selectFrom(NATURE).where(NATURE.ACTIF).fetchInto()

    fun getNatureForSelect(): List<IdLibelleNature> =
        dsl.select(NATURE.ID, NATURE.LIBELLE).from(NATURE).orderBy(NATURE.LIBELLE).fetchInto()

    data class IdLibelleNature(
        val natureId: UUID,
        val natureLibelle: String,
    )

    data class Filter(
        val natureActif: Boolean?,
        val natureCode: String?,
        val natureLibelle: String?,
        val natureTypePei: TypePei?,
        val natureProtected: Boolean?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                natureActif?.let { DSL.and(NATURE.ACTIF.eq(natureActif)) },
                natureCode?.let { DSL.and(NATURE.CODE.contains(natureCode)) },
                natureLibelle?.let { DSL.and(NATURE.LIBELLE.contains(natureLibelle)) },
                natureTypePei?.let { DSL.and(NATURE.TYPE_PEI.eq(natureTypePei)) },
                natureProtected?.let { DSL.and(NATURE.PROTECTED.eq(natureProtected)) },
            ),

        )
    }

    data class Sort(
        val natureActif: Int?,
        val natureCode: Int?,
        val natureLibelle: Int?,
        val natureTypePei: Int?,
        val natureProtected: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            NATURE.ACTIF.getSortField(natureActif),
            NATURE.CODE.getSortField(natureCode),
            NATURE.LIBELLE.getSortField(natureLibelle),
            NATURE.TYPE_PEI.getSortField(natureTypePei),
            NATURE.PROTECTED.getSortField(natureProtected),
        )
    }

    fun getTable(params: Params<Filter, Sort>): Collection<Nature> =
        dsl.select(NATURE.fields().asList()).from(NATURE).where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition() ?: listOf(NATURE.CODE)).limit(params.limit).offset(params.offset)
            .fetchInto()

    fun getCount(params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(NATURE).where(params.filterBy?.toCondition() ?: DSL.trueCondition()).fetchSingleInto()

    fun getById(id: UUID): Nature? =
        dsl.select(NATURE.fields().asList()).from(NATURE).where(NATURE.ID.eq(id)).fetchOneInto()

    fun add(natureData: Nature): Int =
        dsl.insertInto(NATURE, NATURE.ID, NATURE.ACTIF, NATURE.CODE, NATURE.LIBELLE, NATURE.TYPE_PEI, NATURE.PROTECTED)
            .values(
                natureData.natureId,
                natureData.natureActif,
                natureData.natureCode,
                natureData.natureLibelle,
                natureData.natureTypePei,
                false,
            ).execute()

    fun edit(natureData: Nature): Int =
        dsl.update(NATURE).set(NATURE.ACTIF, natureData.natureActif).set(NATURE.CODE, natureData.natureCode)
            .set(NATURE.LIBELLE, natureData.natureLibelle).set(NATURE.TYPE_PEI, natureData.natureTypePei)
            .where(NATURE.ID.eq(natureData.natureId)).and(NATURE.PROTECTED.isFalse).execute()

    fun remove(id: UUID): Int = dsl.deleteFrom(NATURE).where(NATURE.ID.eq(id)).and(NATURE.PROTECTED.isFalse).execute()
}
