package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.OldebProprietaire
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETAIRE
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETE
import java.util.UUID
import kotlin.math.absoluteValue

class ProprietaireRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getList(params: Params<Filter, Sort>): List<OldebProprietaire> =
        dsl.selectFrom(OLDEB_PROPRIETAIRE)
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition())
            .offset(params.offset)
            .limit(params.limit)
            .fetchInto()

    fun getCount(filterBy: Filter?): Int =
        dsl.selectCount().from(OLDEB_PROPRIETAIRE)
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    fun getProprietaireForSelect(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            OLDEB_PROPRIETAIRE.ID.`as`("id"),
            OLDEB_PROPRIETAIRE.ID.`as`("code"),
            DSL.concat(OLDEB_PROPRIETAIRE.NOM, DSL.`val`(" "), OLDEB_PROPRIETAIRE.PRENOM, DSL.`val`(" ("), OLDEB_PROPRIETAIRE.CIVILITE, DSL.`val`(")")).`as`("libelle"),
        )
            .from(OLDEB_PROPRIETAIRE)
            .orderBy(OLDEB_PROPRIETAIRE.NOM, OLDEB_PROPRIETAIRE.PRENOM)
            .fetchInto()

    fun get(proprietaireId: UUID): OldebProprietaire =
        dsl.selectFrom(OLDEB_PROPRIETAIRE).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaireId)).fetchSingleInto()

    fun insertProprietaire(proprietaire: OldebProprietaire): Int =
        dsl.insertInto(OLDEB_PROPRIETAIRE).set(dsl.newRecord(OLDEB_PROPRIETAIRE, proprietaire)).execute()

    fun updateProprietaire(proprietaire: OldebProprietaire): Int =
        dsl.update(OLDEB_PROPRIETAIRE).set(dsl.newRecord(OLDEB_PROPRIETAIRE, proprietaire)).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaire.oldebProprietaireId)).execute()

    fun deleteProprietaire(proprietaireId: UUID): Int =
        dsl.deleteFrom(OLDEB_PROPRIETAIRE).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaireId)).execute()

    fun isProprietaireInUse(proprietaireId: UUID): Boolean =
        dsl.fetchExists(
            dsl.select(OLDEB_PROPRIETE.OLDEB_PROPRIETAIRE_ID).from(OLDEB_PROPRIETE).where(OLDEB_PROPRIETE.OLDEB_PROPRIETAIRE_ID.eq(proprietaireId)),
        )

    data class Filter(
        val oldebProprietaireNom: String?,
        val oldebProprietairePrenom: String?,
        val oldebProprietaireVille: String?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    oldebProprietaireNom?.let { OLDEB_PROPRIETAIRE.NOM.containsIgnoreCaseUnaccent(it) },
                    oldebProprietairePrenom?.let { OLDEB_PROPRIETAIRE.PRENOM.containsIgnoreCaseUnaccent(it) },
                    oldebProprietaireVille?.let { OLDEB_PROPRIETAIRE.VILLE.containsIgnoreCaseUnaccent(it) },
                ),
            )
    }

    data class Sort(
        val oldebProprietaireNom: Int?,
        val oldebProprietairePrenom: Int?,
        val oldebProprietaireVille: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            oldebProprietaireNom?.let { "oldebProprietaireNom" to it },
            oldebProprietairePrenom?.let { "oldebProprietairePrenom" to it },
            oldebProprietaireVille?.let { "oldebProprietaireVille" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "oldebProprietaireNom" -> OLDEB_PROPRIETAIRE.NOM.getSortField(pair.second)
                "oldebProprietairePrenom" -> OLDEB_PROPRIETAIRE.PRENOM.getSortField(pair.second)
                "oldebProprietaireVille" -> OLDEB_PROPRIETAIRE.VILLE.getSortField(pair.second)
                else -> null
            }
        }
    }
}
