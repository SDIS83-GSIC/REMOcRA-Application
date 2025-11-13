package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.data.TypeCriseCategorieData
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT_CATEGORIE
import remocra.db.jooq.remocra.tables.references.EVENEMENT_SOUS_CATEGORIE
import java.util.UUID
import kotlin.math.absoluteValue

class EvenementSousCategorieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<TypeCriseCategorieData> =
        dsl.select(
            EVENEMENT_SOUS_CATEGORIE.ID,
            EVENEMENT_SOUS_CATEGORIE.CODE,
            EVENEMENT_SOUS_CATEGORIE.LIBELLE,
            EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE,
            EVENEMENT_CATEGORIE.ID,
            EVENEMENT_CATEGORIE.LIBELLE,
        ).from(EVENEMENT_SOUS_CATEGORIE)
            .join(EVENEMENT_CATEGORIE)
            .on(EVENEMENT_CATEGORIE.ID.eq(EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: setOf(EVENEMENT_SOUS_CATEGORIE.LIBELLE.asc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?): Int =
        dsl.selectCount()
            .from(EVENEMENT_SOUS_CATEGORIE)
            .join(EVENEMENT_CATEGORIE)
            .on(EVENEMENT_CATEGORIE.ID.eq(EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID))
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    data class Filter(
        val evenementSousCategorieCode: String?,
        val evenementSousCategorieLibelle: String?,
        val evenementSousCategorieTypeGeometrie: TypeGeometry?,
        val evenementCategorieLibelle: String?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                evenementSousCategorieCode?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.CODE.containsIgnoreCaseUnaccent(it)) },
                evenementSousCategorieLibelle?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                evenementSousCategorieTypeGeometrie?.let { DSL.and(EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE.eq(it)) },
                evenementCategorieLibelle?.let { DSL.and(EVENEMENT_CATEGORIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
            ),
        )
    }

    data class Sort(
        val evenementSousCategorieCode: Int?,
        val evenementSousCategorieLibelle: Int?,
        val evenementSousCategorieTypeGeometrie: Int?,
        val evenementCategorieLibelle: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            evenementSousCategorieCode?.let { "evenementSousCategorieCode" to it },
            evenementSousCategorieLibelle?.let { "evenementSousCategorieLibelle" to it },
            evenementSousCategorieTypeGeometrie?.let { "evenementSousCategorieTypeGeometrie" to it },
            evenementCategorieLibelle?.let { "evenementCategorieLibelle" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "evenementSousCategorieCode" -> EVENEMENT_SOUS_CATEGORIE.CODE.getSortField(pair.second)
                "evenementSousCategorieLibelle" -> EVENEMENT_SOUS_CATEGORIE.LIBELLE.getSortField(pair.second)
                "evenementSousCategorieTypeGeometrie" -> EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE.getSortField(pair.second)
                "evenementCategorieLibelle" -> EVENEMENT_CATEGORIE.LIBELLE.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun insert(evenementSousCategorie: EvenementSousCategorie) =
        dsl.insertInto(EVENEMENT_SOUS_CATEGORIE)
            .set(dsl.newRecord(EVENEMENT_SOUS_CATEGORIE, evenementSousCategorie))
            .execute()

    fun update(evenementSousCategorie: EvenementSousCategorie) =
        dsl.update(EVENEMENT_SOUS_CATEGORIE)
            .set(dsl.newRecord(EVENEMENT_SOUS_CATEGORIE, evenementSousCategorie))
            .where(EVENEMENT_SOUS_CATEGORIE.ID.eq(evenementSousCategorie.evenementSousCategorieId))
            .execute()

    fun getById(evenementSousCategorie: UUID): EvenementSousCategorie =
        dsl.selectFrom(EVENEMENT_SOUS_CATEGORIE)
            .where(EVENEMENT_SOUS_CATEGORIE.ID.eq(evenementSousCategorie))
            .fetchSingleInto()

    fun delete(evenementSousCategorieId: UUID) =
        dsl.deleteFrom(EVENEMENT_SOUS_CATEGORIE)
            .where(EVENEMENT_SOUS_CATEGORIE.ID.eq(evenementSousCategorieId))
            .execute()

    fun fetchExistsInEvenement(evenementSousCategorieId: UUID) =
        dsl.fetchExists(dsl.select(EVENEMENT.ID).from(EVENEMENT).where(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(evenementSousCategorieId)))
}
