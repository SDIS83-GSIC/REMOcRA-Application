package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.CriseData
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.EVENEMENT
import remocra.db.jooq.remocra.tables.references.L_CRISE_COMMUNE
import remocra.db.jooq.remocra.tables.references.L_CRISE_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_EVENEMENT_DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_TOPONYMIE_CRISE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE
import remocra.db.jooq.remocra.tables.references.TYPE_TOPONYMIE
import java.time.ZonedDateTime
import java.util.UUID

class CriseRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getCrises(params: Params<FilterCrise, SortCrise>): Collection<CriseComplete> =
        dsl.select(
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.STATUT_TYPE,
            TYPE_CRISE.LIBELLE.`as`("typeCriseLibelle"),
            multiset(
                selectDistinct(COMMUNE.LIBELLE)
                    .from(COMMUNE)
                    .join(L_CRISE_COMMUNE)
                    .on(L_CRISE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_CRISE_COMMUNE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1().toString()
                }
            }.`as`("listeCommune"),
        )
            .from(CRISE)
            .join(TYPE_CRISE)
            .on(CRISE.TYPE_CRISE_ID.eq(TYPE_CRISE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(CRISE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun getCountDocumentFromCrise(criseId: UUID, filterBy: FilterCrise?): Int {
        val countFromCrise = dsl.selectCount()
            .from(DOCUMENT)
            .join(L_CRISE_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_CRISE_DOCUMENT.DOCUMENT_ID))
            .where(L_CRISE_DOCUMENT.CRISE_ID.eq(criseId))
            .and(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto<Int>()

        val countFromEvenement = dsl.selectCount()
            .from(DOCUMENT)
            .join(L_EVENEMENT_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
            .join(EVENEMENT)
            .on(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID))
            .where(EVENEMENT.CRISE_ID.eq(criseId))
            .fetchSingleInto<Int>()

        return countFromCrise + countFromEvenement
    }

    fun getCountCrises(filterBy: FilterCrise?): Int =
        dsl.selectCount()
            .from(CRISE)
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()

    data class CriseComplete(
        val criseId: UUID,
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseDateDebut: ZonedDateTime?,
        val criseDateFin: ZonedDateTime?,
        val criseStatutType: String?,
        val typeCriseLibelle: String?,
        var listeCommune: Collection<String>?,
    )

    data class TypeCriseComplete(
        val criseId: String?,
        val criseNom: String?,
    )

    data class FilterCrise(
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseStatutType: TypeCriseStatut?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    criseLibelle?.let { DSL.and(CRISE.LIBELLE.contains(it)) },
                    criseDescription?.let { DSL.and(CRISE.DESCRIPTION.contains(it)) },
                    criseStatutType?.let { DSL.and(CRISE.STATUT_TYPE.eq(it)) },
                ),
            )
    }

    data class SortCrise(
        val criseLibelle: Int?,
        val criseDescription: Int?,
        val criseDateDebut: Int?,
        val criseDateFin: Int?,
        val criseStatutType: Int?,
        val typeCriseLibelle: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            TYPE_CRISE.LIBELLE.getSortField(typeCriseLibelle),
            CRISE.LIBELLE.getSortField(criseLibelle),
            CRISE.DESCRIPTION.getSortField(criseDescription),
            CRISE.DATE_DEBUT.getSortField(criseDateDebut),
            CRISE.DATE_FIN.getSortField(criseDateFin),
            CRISE.STATUT_TYPE.getSortField(criseStatutType),

        )
    }

    fun getCriseForSelect(): Collection<TypeCriseComplete> =
        dsl.select(TYPE_CRISE.ID.`as`("criseId"), TYPE_CRISE.LIBELLE.`as`("criseNom"))
            .from(TYPE_CRISE)
            .orderBy(TYPE_CRISE.LIBELLE)
            .fetchInto()

    fun createCrise(criseData: CriseData): Int =
        // insérer dans les crises
        dsl.insertInto(
            CRISE,
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.TYPE_CRISE_ID,
            CRISE.STATUT_TYPE,
        ).values(
            criseData.criseId,
            criseData.criseLibelle,
            criseData.criseDescription,
            criseData.criseDateDebut,
            criseData.criseDateFin,
            criseData.criseTypeCriseId,
            criseData.criseStatutType,
        ).execute()

    fun insertLCriseCommune(criseId: UUID, listeCommuneId: Collection<UUID>?) =
        dsl.batch(
            listeCommuneId?.map {
                DSL.insertInto(L_CRISE_COMMUNE)
                    .set(L_CRISE_COMMUNE.CRISE_ID, criseId)
                    .set(L_CRISE_COMMUNE.COMMUNE_ID, it)
            },
        )
            .execute()

    data class CriseUpsert(
        val criseId: UUID,
        val criseLibelle: String?,
        val criseDescription: String?,
        val criseDateDebut: ZonedDateTime,
        val criseDateFin: ZonedDateTime?,
        val criseStatutType: TypeCriseStatut?,
        val typeCriseId: UUID,
        var listeCommune: Collection<UUID>?,
        var listeToponymie: Collection<UUID>?,
    )

    fun getCrise(criseId: UUID): CriseUpsert =
        dsl.select(
            CRISE.ID,
            CRISE.LIBELLE,
            CRISE.DESCRIPTION,
            CRISE.DATE_DEBUT,
            CRISE.DATE_FIN,
            CRISE.STATUT_TYPE,
            TYPE_CRISE.ID,
            multiset(
                selectDistinct(COMMUNE.ID)
                    .from(COMMUNE)
                    .join(L_CRISE_COMMUNE)
                    .on(L_CRISE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_CRISE_COMMUNE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1() as UUID
                }
            }.`as`("listeCommune"),
            multiset(
                selectDistinct(TYPE_TOPONYMIE.ID)
                    .from(TYPE_TOPONYMIE)
                    .join(L_TOPONYMIE_CRISE)
                    .on(L_TOPONYMIE_CRISE.TYPE_TOPONYMIE_ID.eq(TYPE_TOPONYMIE.ID))
                    .where(L_TOPONYMIE_CRISE.CRISE_ID.eq(CRISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1() as UUID
                }
            }.`as`("listeToponymie"),
        ).from(CRISE)
            .join(TYPE_CRISE)
            .on(CRISE.TYPE_CRISE_ID.eq(TYPE_CRISE.ID))
            .where(CRISE.ID.eq(criseId))
            .fetchSingleInto()

    fun updateCrise(
        criseId: UUID,
        criseLibelle: String?,
        criseDescription: String?,
        criseDateDebut: ZonedDateTime?,
        criseDateFin: ZonedDateTime?,
        criseTypeCriseId: UUID?,
        criseStatutType: TypeCriseStatut,
    ) =
        dsl.update(CRISE)
            .set(CRISE.LIBELLE, criseLibelle)
            .set(CRISE.DESCRIPTION, criseDescription)
            .set(CRISE.DATE_DEBUT, criseDateDebut)
            .set(CRISE.DATE_FIN, criseDateFin)
            .set(CRISE.TYPE_CRISE_ID, criseTypeCriseId)
            .set(CRISE.STATUT_TYPE, criseStatutType)
            .where(CRISE.ID.eq(criseId))
            .execute()

    fun deleleteLCriseCommune(criseId: UUID) =
        dsl.deleteFrom(L_CRISE_COMMUNE)
            .where(L_CRISE_COMMUNE.CRISE_ID.eq(criseId))
            .execute()

    fun deleteLToponymieCrise(criseId: UUID) =
        dsl.deleteFrom(L_TOPONYMIE_CRISE)
            .where(L_TOPONYMIE_CRISE.CRISE_ID.eq(criseId))
            .execute()

    fun cloreCrise(criseId: UUID, criseDateFin: ZonedDateTime?) =
        dsl.update(CRISE)
            .set(CRISE.STATUT_TYPE, TypeCriseStatut.TERMINEE)
            .set(CRISE.DATE_FIN, criseDateFin)
            .where(CRISE.ID.eq(criseId))
            .execute()

    data class CriseDocs(
        val documentId: UUID,
        val documentDate: ZonedDateTime?,
        val documentNomFichier: String?,
        val type: String?,
    )

    /**
     * Récupère les documents associés à une crise ou à un événement avec un type d'origine qui permet de différencier les documents issus d'une crise ou d'un événement.
     *
     * @param criseId L'ID de la crise pour filtrer les documents associés.
     * @return Une liste de documents
     */
    fun getAllDocumentsFromCrise(criseId: UUID, params: Params<FilterCrise, SortCrise>): Collection<CriseDocs> =
        dsl.select(
            DOCUMENT.ID,
            DOCUMENT.NOM_FICHIER,
            DOCUMENT.DATE,
            DSL.case_()
                .`when`(L_CRISE_DOCUMENT.CRISE_ID.isNotNull(), DSL.`val`("Crise"))
                .`when`(EVENEMENT.CRISE_ID.isNotNull(), DSL.`val`("Évènement"))
                .`as`("type"),
        )
            .from(DOCUMENT)
            .leftJoin(L_CRISE_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_CRISE_DOCUMENT.DOCUMENT_ID))
            .leftJoin(L_EVENEMENT_DOCUMENT)
            .on(DOCUMENT.ID.eq(L_EVENEMENT_DOCUMENT.DOCUMENT_ID))
            .leftJoin(EVENEMENT)
            .on(L_EVENEMENT_DOCUMENT.EVENEMENT_ID.eq(EVENEMENT.ID))
            .where(
                L_CRISE_DOCUMENT.CRISE_ID.eq(criseId)
                    .or(EVENEMENT.CRISE_ID.eq(criseId)),
            )
            .and(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(DOCUMENT.DATE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()
}
