package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.data.groupecouche.GroupeCoucheTableData
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import kotlin.math.absoluteValue

class GroupeCoucheRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAllForAdmin(params: Params<FilterGroupeCouche, Sort>): List<GroupeCoucheTableData> =
        dsl.selectDistinct(
            GROUPE_COUCHE.ID,
            GROUPE_COUCHE.CODE,
            GROUPE_COUCHE.ORDRE,
            GROUPE_COUCHE.LIBELLE,
            GROUPE_COUCHE.PROTECTED,
            DSL.countDistinct(COUCHE.ID).`as`("nombreCouche"),
        )
            .from(GROUPE_COUCHE)
            .leftJoin(COUCHE).on(COUCHE.GROUPE_COUCHE_ID.eq(GROUPE_COUCHE.ID))
            .where(params.filterBy?.toCondition())
            .groupBy(
                GROUPE_COUCHE.ID,
                GROUPE_COUCHE.CODE,
                GROUPE_COUCHE.ORDRE,
                GROUPE_COUCHE.LIBELLE,
                GROUPE_COUCHE.PROTECTED,
            )
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(GROUPE_COUCHE.ORDRE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countForAdmin(filterBy: FilterGroupeCouche?): Int =
        dsl.selectCount().from(GROUPE_COUCHE)
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    data class FilterGroupeCouche(
        val groupeCoucheCode: String?,
        val groupeCoucheLibelle: String?,
        val groupeCoucheProtected: Boolean?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                groupeCoucheCode?.let { DSL.and(GROUPE_COUCHE.CODE.containsIgnoreCaseUnaccent(it)) },
                groupeCoucheLibelle?.let { DSL.and(GROUPE_COUCHE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                groupeCoucheProtected?.let { DSL.and(GROUPE_COUCHE.PROTECTED.eq(it)) },
            ),
        )
    }

    data class Sort(
        val groupeCoucheCode: Int?,
        val groupeCoucheLibelle: Int?,
        val groupeCoucheProtected: Int?,
        val nombreCouche: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            groupeCoucheCode?.let { "groupeCoucheCode" to it },
            groupeCoucheLibelle?.let { "groupeCoucheLibelle" to it },
            groupeCoucheProtected?.let { "groupeCoucheProtected" to it },
            nombreCouche?.let { "nombreCouche" to it },
        )
        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "groupeCoucheCode" -> GROUPE_COUCHE.CODE.getSortField(pair.second)
                "groupeCoucheLibelle" -> GROUPE_COUCHE.LIBELLE.getSortField(pair.second)
                "groupeCoucheProtected" -> GROUPE_COUCHE.PROTECTED.getSortField(pair.second)
                "nombreCouche" -> DSL.countDistinct(COUCHE.ID).getSortField(pair.second)
                else -> null
            }
        }
    }
}
