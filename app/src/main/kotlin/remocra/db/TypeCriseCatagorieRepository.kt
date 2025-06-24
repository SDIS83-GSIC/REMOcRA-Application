package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.data.TypeCriseCategorieData
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.TypeCriseCategorie
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE_CATEGORIE

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
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            TYPE_CRISE_CATEGORIE.CODE.getSortField(typeCriseCategorieCode),
            TYPE_CRISE_CATEGORIE.LIBELLE.getSortField(typeCriseCategorieLibelle),
            TYPE_CRISE_CATEGORIE.TYPE_GEOMETRIE.getSortField(typeCriseCategorieTypeGeometrie),
            CRISE_CATEGORIE.LIBELLE.getSortField(criseCategorieLibelle),
        )
    }

    fun insert(typeCriseCategorie: TypeCriseCategorie) =
        dsl.insertInto(TYPE_CRISE_CATEGORIE)
            .set(dsl.newRecord(TYPE_CRISE_CATEGORIE, typeCriseCategorie))
            .execute()
}
