package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.db.jooq.remocra.tables.references.DFCI_CONFLIT
import java.util.UUID
import kotlin.math.absoluteValue

class DfciConflitRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Insère un nouveau conflit dans la table
     */
    fun insertDfciConflit(dfciConflit: DfciConflit) =
        dsl.insertInto(DFCI_CONFLIT).set(dsl.newRecord(DFCI_CONFLIT, dfciConflit)).execute()

    /**
     * Vérifie si un conflit existe déjà dans la table
     */
    fun getExistDfciConflit(elementId: UUID, champ: String): Boolean =
        dsl
            .fetchExists(
                DFCI_CONFLIT,
                DFCI_CONFLIT.ELEMENT_ID.eq(elementId),
                DFCI_CONFLIT.CHAMP.eq(champ),
            )

    /**
     * Met à jour le conflit existant avec ses nouvelles données
     */
    fun updateDfciConflit(dfciConflit: DfciConflit) =
        dsl
            .update(DFCI_CONFLIT)
            .set(DFCI_CONFLIT.VALEUR_REMOCRA, dfciConflit.dfciConflitValeurRemocra)
            .set(DFCI_CONFLIT.VALEUR_SIG, dfciConflit.dfciConflitValeurSig)
            .set(DFCI_CONFLIT.DATE, dfciConflit.dfciConflitDate)
            .where(DFCI_CONFLIT.ELEMENT_ID.eq(dfciConflit.dfciConflitElementId))
            .and(DFCI_CONFLIT.CHAMP.eq(dfciConflit.dfciConflitChamp))
            .execute()

    fun getAllDfciConflit(params: Params<FilterDfciConflit, SortDfciConflit>): List<DfciConflit> =
        dsl
            .selectFrom(DFCI_CONFLIT)
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition())
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountAllDfciConflit(filterBy: FilterDfciConflit?): Int =
        dsl
            .selectCount()
            .from(DFCI_CONFLIT)
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()

    fun getDfciConflitById(dfciConflitId: UUID): DfciConflit =
        dsl
            .selectFrom(DFCI_CONFLIT)
            .where(DFCI_CONFLIT.ID.eq(dfciConflitId))
            .fetchSingleInto()

    fun dropDfciConflit(dfciConflit: DfciConflit) =
        dsl.deleteFrom(DFCI_CONFLIT).where(DFCI_CONFLIT.ID.eq(dfciConflit.dfciConflitId)).execute()

    data class SortDfciConflit(
        val dfciConflitTable: Int?,
        val dfciConflitElementId: Int?,
        val dfciConflitChamp: Int?,
    ) {

        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            dfciConflitTable?.let { "dfciConflitTable" to it },
            dfciConflitElementId?.let { "dfciConflitElementId" to it },
            dfciConflitChamp?.let { "dfciConflitChamp" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "dfciConflitTable" -> DFCI_CONFLIT.TABLE.getSortField(pair.second)
                "dfciConflitElementId" -> DFCI_CONFLIT.ELEMENT_ID.getSortField(dfciConflitElementId)
                "dfciConflitChamp" -> DFCI_CONFLIT.CHAMP.getSortField(dfciConflitChamp)
                else -> null
            }
        }
    }

    data class FilterDfciConflit(
        val dfciConflitTable: String?,
        val dfciConflitElementId: String?,
        val dfciConflitChamp: String?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    dfciConflitTable?.let { DSL.and(DFCI_CONFLIT.TABLE.contains(it)) },
                    dfciConflitElementId?.let { DSL.and(DFCI_CONFLIT.ELEMENT_ID.cast<String>().contains(it)) },
                    dfciConflitChamp?.let { DSL.and(DFCI_CONFLIT.CHAMP.contains(it)) },
                ),
            )
    }
}
