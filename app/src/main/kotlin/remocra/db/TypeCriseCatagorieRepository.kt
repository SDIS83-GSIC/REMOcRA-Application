package remocra.db

import com.google.inject.Inject
import kotlinx.coroutines.selects.select
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.data.TypeCriseCategorieData
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.TypeCriseCategorie
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE_CATEGORIE
import java.util.UUID
import kotlin.math.absoluteValue

class TypeCriseCatagorieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<TypeCriseCategorieData> =
        dsl.select(
            TYPE_CRISE_CATEGORIE.ID,
            TYPE_CRISE_CATEGORIE.CODE,
            TYPE_CRISE_CATEGORIE.LIBELLE,
            TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE,
            CRISE_CATEGORIE.ID,
            CRISE_CATEGORIE.LIBELLE,
        ).from(TYPE_CRISE_CATEGORIE)
            .join(CRISE_CATEGORIE)
            .on(CRISE_CATEGORIE.ID.eq(TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID))
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: setOf(TYPE_CRISE_CATEGORIE.LIBELLE.asc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?): Int =
        dsl.selectCount()
            .from(TYPE_CRISE_CATEGORIE)
            .join(CRISE_CATEGORIE)
            .on(CRISE_CATEGORIE.ID.eq(TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID))
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    data class Filter(
        val typeCriseCategorieCode: String?,
        val typeCriseCategorieLibelle: String?,
        val typeCriseCategorieTypeGeometrie: TypeGeometry?,
        val criseCategorieLibelle: String?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                typeCriseCategorieCode?.let { DSL.and(TYPE_CRISE_CATEGORIE.CODE.containsIgnoreCaseUnaccent(it)) },
                typeCriseCategorieLibelle?.let { DSL.and(TYPE_CRISE_CATEGORIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                typeCriseCategorieTypeGeometrie?.let { DSL.and(TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE.eq(it)) },
                criseCategorieLibelle?.let { DSL.and(CRISE_CATEGORIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
            ),
        )
    }

    data class Sort(
        val typeCriseCategorieCode: Int?,
        val typeCriseCategorieLibelle: Int?,
        val typeCriseCategorieTypeGeometrie: Int?,
        val criseCategorieLibelle: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            typeCriseCategorieCode?.let { "typeCriseCategorieCode" to it },
            typeCriseCategorieLibelle?.let { "typeCriseCategorieLibelle" to it },
            typeCriseCategorieTypeGeometrie?.let { "typeCriseCategorieTypeGeometrie" to it },
            criseCategorieLibelle?.let { "criseCategorieLibelle" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "typeCriseCategorieCode" -> TYPE_CRISE_CATEGORIE.CODE.getSortField(pair.second)
                "typeCriseCategorieLibelle" -> TYPE_CRISE_CATEGORIE.LIBELLE.getSortField(pair.second)
                "typeCriseCategorieTypeGeometrie" -> TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE.getSortField(pair.second)
                "criseCategorieLibelle" -> CRISE_CATEGORIE.LIBELLE.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun insert(typeCriseCategorie: TypeCriseCategorie) =
        dsl.insertInto(TYPE_CRISE_CATEGORIE)
            .set(dsl.newRecord(TYPE_CRISE_CATEGORIE, typeCriseCategorie))
            .execute()

    fun update(typeCriseCategorie: TypeCriseCategorie) =
        dsl.update(TYPE_CRISE_CATEGORIE)
            .set(dsl.newRecord(TYPE_CRISE_CATEGORIE, typeCriseCategorie))
            .where(TYPE_CRISE_CATEGORIE.ID.eq(typeCriseCategorie.typeCriseCategorieId))
            .execute()

    fun getById(typeCriseCategorieId: UUID): TypeCriseCategorie =
        dsl.selectFrom(TYPE_CRISE_CATEGORIE)
            .where(TYPE_CRISE_CATEGORIE.ID.eq(typeCriseCategorieId))
            .fetchSingleInto()

    fun delete(typeCriseCategorieId: UUID) =
        dsl.deleteFrom(TYPE_CRISE_CATEGORIE)
            .where(TYPE_CRISE_CATEGORIE.ID.eq(typeCriseCategorieId))
            .execute()

    fun fetchExistsInEvenement(typeCriseCategorieId: UUID) =
        dsl.fetchExists(dsl.select(EVENEMENT.ID).from(EVENEMENT).where(EVENEMENT.TYPE_CRISE_CATEGORIE_ID.eq(typeCriseCategorieId)))
}
