package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.EvenementSousCategoryData
import remocra.data.Params
import remocra.data.SousCategorieComplement
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.enums.TypeParametreEvenementComplement
import remocra.db.jooq.remocra.tables.pojos.CriseEvenementComplement
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.db.jooq.remocra.tables.pojos.LEvenementCriseEvenementComplement
import remocra.db.jooq.remocra.tables.references.CRISE_EVENEMENT_COMPLEMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT_CATEGORIE
import remocra.db.jooq.remocra.tables.references.EVENEMENT_SOUS_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT
import java.util.UUID
import kotlin.math.absoluteValue

class EvenementSousCategorieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<EvenementSousCategoryData> =
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

    fun getById(evenementSousCategorieId: UUID): EvenementSousCategorieWithComplementData = dsl.select(
        EVENEMENT_SOUS_CATEGORIE.ID,
        EVENEMENT_SOUS_CATEGORIE.CODE,
        EVENEMENT_SOUS_CATEGORIE.LIBELLE,
        EVENEMENT_SOUS_CATEGORIE.TYPE_GEOMETRIE,
        EVENEMENT_SOUS_CATEGORIE.EVENEMENT_CATEGORIE_ID,
        EVENEMENT_SOUS_CATEGORIE.ACTIF,
        multiset(
            selectDistinct(
                CRISE_EVENEMENT_COMPLEMENT.ID,
                CRISE_EVENEMENT_COMPLEMENT.EVENEMENT_SOUS_CATEGORIE_ID,
                CRISE_EVENEMENT_COMPLEMENT.LIBELLE,
                CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL,
                CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL_ID,
                CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL_LIBELLE,
                CRISE_EVENEMENT_COMPLEMENT.VALEUR_DEFAUT,
                CRISE_EVENEMENT_COMPLEMENT.EST_REQUIS,
                CRISE_EVENEMENT_COMPLEMENT.TYPE,
            )
                .from(CRISE_EVENEMENT_COMPLEMENT)
                .where(CRISE_EVENEMENT_COMPLEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(EVENEMENT_SOUS_CATEGORIE.ID)),
        ).convertFrom { record ->
            record?.map { r ->
                SousCategorieComplement(
                    sousCategorieComplementId = r.value1() as UUID,
                    evenementSousCategorieId = r.value2() as UUID,
                    sousCategorieComplementLibelle = r.value3().toString(),
                    sousCategorieComplementSql = r.value4().toString(),
                    sousCategorieComplementSqlId = r.value5().toString(),
                    sousCategorieComplementSqlLibelle = r.value6().toString(),
                    sousCategorieComplementValeurDefaut = r.value7().toString(),
                    sousCategorieComplementEstRequis = r.value8() as Boolean,
                    sousCategorieComplementType = r.value9() as TypeParametreEvenementComplement,
                )
            }
        }
            .`as`("evenementSousCategorieComplement"),

    )
        .from(EVENEMENT_SOUS_CATEGORIE)
        .where(EVENEMENT_SOUS_CATEGORIE.ID.eq(evenementSousCategorieId))
        .fetchSingleInto()

    fun delete(evenementSousCategorieId: UUID) =
        dsl.deleteFrom(EVENEMENT_SOUS_CATEGORIE)
            .where(EVENEMENT_SOUS_CATEGORIE.ID.eq(evenementSousCategorieId))
            .execute()

    fun fetchExistsInEvenement(evenementSousCategorieId: UUID) =
        dsl.fetchExists(dsl.select(EVENEMENT.ID).from(EVENEMENT).where(EVENEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(evenementSousCategorieId)))

    fun upsertSousTypeParametre(criseEvenementComplement: CriseEvenementComplement) {
        val record = dsl.newRecord(CRISE_EVENEMENT_COMPLEMENT, criseEvenementComplement)
        dsl.insertInto(CRISE_EVENEMENT_COMPLEMENT)
            .set(record)
            .onConflict(CRISE_EVENEMENT_COMPLEMENT.ID)
            .doUpdate()
            .set(CRISE_EVENEMENT_COMPLEMENT.EVENEMENT_SOUS_CATEGORIE_ID, criseEvenementComplement.criseEvenementComplementEvenementSousCategorieId)
            .set(CRISE_EVENEMENT_COMPLEMENT.LIBELLE, criseEvenementComplement.criseEvenementComplementLibelle)
            .set(CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL, criseEvenementComplement.criseEvenementComplementSourceSql)
            .set(CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL_ID, criseEvenementComplement.criseEvenementComplementSourceSqlId)
            .set(CRISE_EVENEMENT_COMPLEMENT.SOURCE_SQL_LIBELLE, criseEvenementComplement.criseEvenementComplementSourceSqlLibelle)
            .set(CRISE_EVENEMENT_COMPLEMENT.VALEUR_DEFAUT, criseEvenementComplement.criseEvenementComplementValeurDefaut)
            .set(CRISE_EVENEMENT_COMPLEMENT.EST_REQUIS, criseEvenementComplement.criseEvenementComplementEstRequis)
            .set(CRISE_EVENEMENT_COMPLEMENT.TYPE, criseEvenementComplement.criseEvenementComplementType)
            .execute()
    }

    fun deleteComplementByEvenementSousCategorieId(evenementSousCategorieId: UUID) {
        dsl.deleteFrom(CRISE_EVENEMENT_COMPLEMENT)
            .where(CRISE_EVENEMENT_COMPLEMENT.EVENEMENT_SOUS_CATEGORIE_ID.eq(evenementSousCategorieId))
            .execute()
    }

    fun upsertEvenementComplement(lEvenementCriseEvenementComplement: LEvenementCriseEvenementComplement) {
        dsl.insertInto(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT)
            .set(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT.EVENEMENT_ID, lEvenementCriseEvenementComplement.evenementId)
            .set(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT.CRISE_EVENEMENT_COMPLEMENT_ID, lEvenementCriseEvenementComplement.criseEvenementComplementId)
            .set(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT.VALEUR, lEvenementCriseEvenementComplement.valeur)
            .onDuplicateKeyUpdate() // correspond à la clé primaire
            .set(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT.VALEUR, lEvenementCriseEvenementComplement.valeur)
            .execute()
    }

    fun deleteCriseEvenementComplementByComplementId(complementId: UUID) {
        dsl.deleteFrom(CRISE_EVENEMENT_COMPLEMENT)
            .where(CRISE_EVENEMENT_COMPLEMENT.ID.eq(complementId))
            .execute()
    }

    fun deleteLEvenementCriseComplementByComplementId(complementId: UUID) {
        dsl.deleteFrom(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT)
            .where(L_EVENEMENT_CRISE_EVENEMENT_COMPLEMENT.CRISE_EVENEMENT_COMPLEMENT_ID.eq(complementId))
            .execute()
    }
}
