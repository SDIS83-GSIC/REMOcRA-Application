package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.EvenementCategorieData
import remocra.data.EvenementCategorieWithTypeCriseData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.EvenementCategorie
import remocra.db.jooq.remocra.tables.pojos.LTypeCriseCategorie
import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import remocra.db.jooq.remocra.tables.references.EVENEMENT_CATEGORIE
import remocra.db.jooq.remocra.tables.references.EVENEMENT_SOUS_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE
import java.util.UUID
import kotlin.math.absoluteValue

class EvenementCategorieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<EvenementCategorie>, AbstractRepository() {

    override fun getMapById(): Map<UUID, EvenementCategorie> = dsl.selectFrom(EVENEMENT_CATEGORIE).where(EVENEMENT_CATEGORIE.ACTIF.isTrue).orderBy(
        EVENEMENT_CATEGORIE.LIBELLE,
    ).fetchInto<EvenementCategorie>().associateBy { it.evenementCategorieId }

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<EvenementCategorieData> =
        dsl.select(
            EVENEMENT_CATEGORIE.ID,
            EVENEMENT_CATEGORIE.CODE,
            EVENEMENT_CATEGORIE.LIBELLE,
            EVENEMENT_CATEGORIE.ACTIF,
            DSL.`when`(
                DSL.exists(
                    DSL.selectOne()
                        .from(EVENEMENT_SOUS_CATEGORIE)
                        .where(EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID.eq(EVENEMENT_CATEGORIE.ID)),
                ),
                true,
            ).otherwise(false).`as`("evenementCategorieEstUtilisee"),
        ).from(EVENEMENT_CATEGORIE)
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: setOf(EVENEMENT_CATEGORIE.LIBELLE.asc()))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?): Int =
        dsl.selectCount()
            .from(EVENEMENT_CATEGORIE)
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    data class Filter(
        val evenementCategorieCode: String?,
        val evenementCategorieLibelle: String?,
        val evenementCategorieTypeCriseId: UUID?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                evenementCategorieCode?.let { DSL.and(EVENEMENT_CATEGORIE.CODE.containsIgnoreCaseUnaccent(it)) },
                evenementCategorieLibelle?.let { DSL.and(EVENEMENT_CATEGORIE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                evenementCategorieTypeCriseId?.let { typeCriseId ->
                    DSL.exists(
                        DSL.selectOne()
                            .from(L_TYPE_CRISE_CATEGORIE)
                            .where(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID.eq(EVENEMENT_CATEGORIE.ID))
                            .and(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID.eq(typeCriseId)),
                    )
                },
            ),
        )
    }

    data class Sort(
        val evenementCategorieCode: Int?,
        val evenementCategorieLibelle: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            evenementCategorieCode?.let { "evenementCategorieCode" to it },
            evenementCategorieLibelle?.let { "evenementCategorieLibelle" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "evenementCategorieCode" -> EVENEMENT_CATEGORIE.CODE.getSortField(pair.second)
                "evenementCategorieLibelle" -> EVENEMENT_CATEGORIE.LIBELLE.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun insert(evenementCategorie: EvenementCategorie) {
        dsl.insertInto(EVENEMENT_CATEGORIE)
            .set(dsl.newRecord(EVENEMENT_CATEGORIE, evenementCategorie))
            .execute()
    }

    fun update(evenementCategorie: EvenementCategorie) {
        dsl.update(EVENEMENT_CATEGORIE)
            .set(dsl.newRecord(EVENEMENT_CATEGORIE, evenementCategorie))
            .where(EVENEMENT_CATEGORIE.ID.eq(evenementCategorie.evenementCategorieId))
            .execute()
    }

    fun getById(evenementCategorieId: UUID): EvenementCategorieWithTypeCriseData = dsl.select(
        EVENEMENT_CATEGORIE.ID,
        EVENEMENT_CATEGORIE.CODE,
        EVENEMENT_CATEGORIE.LIBELLE,
        EVENEMENT_CATEGORIE.ACTIF,
        multiset(
            selectDistinct(
                L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID,
                TYPE_CRISE.CODE,
                TYPE_CRISE.LIBELLE,
                TYPE_CRISE.ACTIF,
            )
                .from(L_TYPE_CRISE_CATEGORIE)
                .join(TYPE_CRISE)
                .on(TYPE_CRISE.ID.eq(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID))
                .where(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID.eq(EVENEMENT_CATEGORIE.ID)),
        ).convertFrom { record ->
            record?.map { r ->
                TypeCrise(
                    typeCriseId = r.get<UUID>(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID),
                    typeCriseCode = r.get<String>(TYPE_CRISE.CODE),
                    typeCriseLibelle = r.get<String>(TYPE_CRISE.LIBELLE),
                    typeCriseActif = r.get<Boolean>(TYPE_CRISE.ACTIF),
                )
            }
        }
            .`as`("evenementCategorieTypeCrise"),
    )
        .from(EVENEMENT_CATEGORIE)
        .where(EVENEMENT_CATEGORIE.ID.eq(evenementCategorieId))
        .fetchSingleInto()

    fun delete(evenementCategorieId: UUID) {
        dsl.deleteFrom(EVENEMENT_CATEGORIE)
            .where(EVENEMENT_CATEGORIE.ID.eq(evenementCategorieId))
            .execute()
    }

    fun deleteTypeCriseAssociations(evenementCategorieId: UUID) {
        dsl.deleteFrom(L_TYPE_CRISE_CATEGORIE)
            .where(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID.eq(evenementCategorieId))
            .execute()
    }

    fun upsertTypeCriseAssociation(associations: Collection<LTypeCriseCategorie>) {
        if (associations.isEmpty()) return

        dsl.insertInto(
            L_TYPE_CRISE_CATEGORIE,
            L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID,
            L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID,
        )
            .valuesOfRows(
                associations.map {
                    DSL.row(it.typeCriseId, it.criseCategorieId)
                },
            )
            .onConflict(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID, L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID)
            .doUpdate()
            .set(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID, DSL.excluded(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID))
            .set(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID, DSL.excluded(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID))
            .execute()
    }

    fun getTypeCrisesForEvenementCategorie(evenementCategorieId: UUID): Collection<TypeCrise> =
        dsl.select(
            L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID,
            TYPE_CRISE.CODE,
            TYPE_CRISE.LIBELLE,
            TYPE_CRISE.ACTIF,
        )
            .from(L_TYPE_CRISE_CATEGORIE)
            .join(TYPE_CRISE)
            .on(TYPE_CRISE.ID.eq(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID))
            .where(L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID.eq(evenementCategorieId))
            .fetch()
            .map { r ->
                TypeCrise(
                    typeCriseId = r.get<UUID>(L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID),
                    typeCriseCode = r.get<String>(TYPE_CRISE.CODE),
                    typeCriseLibelle = r.get<String>(TYPE_CRISE.LIBELLE),
                    typeCriseActif = r.get<Boolean>(TYPE_CRISE.ACTIF),
                )
            }
}
