package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.case_
import org.jooq.impl.DSL.inline
import org.jooq.impl.DSL.`when`
import remocra.GlobalConstants
import remocra.data.ApiAnomalieWithNature
import remocra.data.GlobalData
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.db.jooq.remocra.tables.pojos.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.L_VISITE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import java.util.UUID

class AnomalieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Anomalie>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Anomalie> = dsl.selectFrom(ANOMALIE).where(ANOMALIE.ACTIF.isTrue).fetchInto<Anomalie>().associateBy { it.anomalieId }

    fun getAllById(): Map<UUID, Anomalie> = dsl.selectFrom(ANOMALIE).where().fetchInto<Anomalie>().associateBy { it.anomalieId }

    fun getAnomalieById(anomalieId: UUID): Anomalie =
        dsl.selectFrom(ANOMALIE).where(ANOMALIE.ID.eq(anomalieId)).fetchSingleInto()

    /**
     * Retourne l'ensemble des anomalies
     */
    fun getAllForAdmin(): Collection<Anomalie> =
        getSystemeForAdmin() + getNotSystemeForAdmin()

    // Ensemble des anomalies système
    private fun getSystemeForAdmin(): Collection<Anomalie> =
        dsl.select(*ANOMALIE.fields(), inline(1).`as`("ens_order"))
            .from(ANOMALIE)
            .join(ANOMALIE_CATEGORIE).on(ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
            .where(ANOMALIE_CATEGORIE.CODE.eq(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME))
            .orderBy(ANOMALIE.LIBELLE)
            .fetchInto()

    // Ensemble des anomalies hors système
    private fun getNotSystemeForAdmin(): Collection<Anomalie> =
        dsl.select(*ANOMALIE.fields(), inline(2).`as`("ens_order"))
            .from(ANOMALIE)
            .join(ANOMALIE_CATEGORIE).on(ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
            .where(ANOMALIE_CATEGORIE.CODE.ne(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME))
            .orderBy(ANOMALIE_CATEGORIE.ORDRE, ANOMALIE.ORDRE)
            .fetchInto()

    /**
     * Retourne l'ensemble des poids/anomalies
     */
    fun getAllAnomaliePoidsForAdmin(): Collection<PoidsAnomalie> =
        dsl.selectFrom(POIDS_ANOMALIE).fetchInto()

    /**
     * Retourne l'ensemble des catégories d'anomalie
     */
    fun getAllAnomalieCategorieForAdmin(): Collection<AnomalieCategorie> =
        dsl.selectFrom(ANOMALIE_CATEGORIE).orderBy(case_(ANOMALIE_CATEGORIE.CODE).`when`(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME, 1000).else_(ANOMALIE_CATEGORIE.ORDRE)).fetchInto()

    /**
     * Retourne l'ensemble des poids/anomalies pour une anomalie
     */
    fun getAnomaliePoidsByAnomalieId(anomalieId: UUID): Collection<PoidsAnomalie> =
        dsl.selectFrom(POIDS_ANOMALIE).where(POIDS_ANOMALIE.ANOMALIE_ID.eq(anomalieId)).fetchInto()

    /**
     * Créer une nouvelle anomalie
     */
    fun insertAnomalie(anomalie: Anomalie): Int =
        dsl.insertInto(ANOMALIE).set(dsl.newRecord(ANOMALIE, anomalie)).execute()

    /**
     * Modifier une anomalie
     */
    fun updateAnomalie(anomalie: Anomalie): Int =
        dsl.update(ANOMALIE)
            .set(ANOMALIE.CODE, anomalie.anomalieCode)
            .set(ANOMALIE.LIBELLE, anomalie.anomalieLibelle)
            .set(ANOMALIE.COMMENTAIRE, anomalie.anomalieCommentaire)
            .set(ANOMALIE.ANOMALIE_CATEGORIE_ID, anomalie.anomalieAnomalieCategorieId)
            .set(ANOMALIE.ACTIF, anomalie.anomalieActif)
            .set(ANOMALIE.REND_NON_CONFORME, anomalie.anomalieRendNonConforme)
            .set(ANOMALIE.POIDS_ANOMALIE_SYSTEME_VAL_INDISPO_TERRESTRE, anomalie.anomaliePoidsAnomalieSystemeValIndispoTerrestre)
            .set(ANOMALIE.POIDS_ANOMALIE_SYSTEME_VAL_INDISPO_HBE, anomalie.anomaliePoidsAnomalieSystemeValIndispoHbe)
            .where(ANOMALIE.ID.eq(anomalie.anomalieId))
            .execute()

    /**
     * Ajouter/modifier un poids/anomalie
     */
    fun upsertPoidsAnomalie(poidsAnomalie: PoidsAnomalie): Int =
        dsl.insertInto(POIDS_ANOMALIE)
            .set(POIDS_ANOMALIE.ID, poidsAnomalie.poidsAnomalieId)
            .set(POIDS_ANOMALIE.ANOMALIE_ID, poidsAnomalie.poidsAnomalieAnomalieId)
            .set(POIDS_ANOMALIE.NATURE_ID, poidsAnomalie.poidsAnomalieNatureId)
            .set(POIDS_ANOMALIE.TYPE_VISITE, poidsAnomalie.poidsAnomalieTypeVisite)
            .set(POIDS_ANOMALIE.VAL_INDISPO_HBE, poidsAnomalie.poidsAnomalieValIndispoHbe)
            .set(POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE, poidsAnomalie.poidsAnomalieValIndispoTerrestre)
            .onDuplicateKeyUpdate()
            .set(POIDS_ANOMALIE.TYPE_VISITE, poidsAnomalie.poidsAnomalieTypeVisite)
            .set(POIDS_ANOMALIE.VAL_INDISPO_HBE, poidsAnomalie.poidsAnomalieValIndispoHbe)
            .set(POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE, poidsAnomalie.poidsAnomalieValIndispoTerrestre)
            .execute()

    /**
     * Supprimer un poids/anomalie
     */
    fun deletePoidsAnomalieByAnomalieId(anomalieId: UUID, excluded: Collection<UUID>? = listOf()): Int =
        dsl.deleteFrom(POIDS_ANOMALIE).where(POIDS_ANOMALIE.ANOMALIE_ID.eq(anomalieId)).and(POIDS_ANOMALIE.ANOMALIE_ID.notIn(excluded)).execute()

    /**
     * Vérifier l''utilisation d'une anomalie
     */
    fun isAnomalieInUse(anomalieId: UUID): Boolean =
        dsl.fetchExists(
            dsl.select(L_VISITE_ANOMALIE.ANOMALIE_ID)
                .from(L_VISITE_ANOMALIE)
                .where(L_VISITE_ANOMALIE.ANOMALIE_ID.eq(anomalieId))
                .union(
                    dsl.select(L_PEI_ANOMALIE.ANOMALIE_ID)
                        .from(L_PEI_ANOMALIE)
                        .where(L_PEI_ANOMALIE.ANOMALIE_ID.eq(anomalieId)),
                ),
        )

    /**
     * Supprimer une anomalie
     */
    fun deleteAnomalie(anomalieId: UUID): Int =
        dsl.deleteFrom(ANOMALIE).where(ANOMALIE.ID.eq(anomalieId)).execute()

    /** Supprimer de la table remocra.l_pei_anomalie toutes les anomalies non-protégées du PEI renseigné
     * @param peiId : UUID
     */
    fun deleteAnomalieNonSystemByPeiId(peiId: UUID) =
        dsl.deleteFrom(L_PEI_ANOMALIE)
            .using(ANOMALIE)
            .where(
                L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID)
                    .and(ANOMALIE.PROTECTED.isFalse)
                    .and(L_PEI_ANOMALIE.PEI_ID.eq(peiId)),
            )
            .execute()

    fun batchInsertLPeiAnomalie(listePeiAnomalie: List<LPeiAnomalie>) {
        dsl.batch(
            listePeiAnomalie.map {
                DSL.insertInto(L_PEI_ANOMALIE).set(dsl.newRecord(L_PEI_ANOMALIE, it)).onConflictDoNothing()
            },
        )
            .execute()
    }

    fun batchInsertLVisiteAnomalie(listeVisiteAnomalie: List<LVisiteAnomalie>) {
        dsl.batch(listeVisiteAnomalie.map { DSL.insertInto(L_VISITE_ANOMALIE).set(dsl.newRecord(L_VISITE_ANOMALIE, it)) })
            .execute()
    }

    /** Condition : conserver uniquement les éléments
     * @param isActive : Boolean?
     * Si actif = true => remonte les anomalies actives dont la catégorie est active
     * Si actif = false => remonte les anomalies inactives ou celles dont la catégorie est inactive
     * Si actif = null => pas de condition => aucun impact sur les résultats
     */
    fun isActiveCondition(isActive: Boolean?): Condition =
        DSL.and(
            listOfNotNull(
                when {
                    isActive == true -> {
                        DSL.and(ANOMALIE.ACTIF.eq(isActive))
                            .and(ANOMALIE_CATEGORIE.ACTIF.eq(isActive))
                    }
                    isActive == false -> {
                        DSL.and(
                            (ANOMALIE.ACTIF.eq(isActive))
                                .or(ANOMALIE_CATEGORIE.ACTIF.eq(isActive)),
                        )
                    }
                    else -> { DSL.noCondition() }
                },
            ),
        )

    /** Remonte les anomalies assignable à un pei selon sa nature
     * Ne remonte pas les anomalies appartenant à la catégorie 'SYSTEME'
     * Un flag pour chaque type_visite indiquent si l'anomalie peut etre assignée lors d'une visite de ce type
     * @param peiId : UUID
     * @return une liste d'objet CompletedAnomalie*/
    fun getAllAnomalieAssignable(peiId: UUID): List<CompletedAnomalie> =
        dsl.select(
            ANOMALIE.ID,
            ANOMALIE.CODE,
            ANOMALIE.LIBELLE,
            ANOMALIE.COMMENTAIRE,
            ANOMALIE.ANOMALIE_CATEGORIE_ID,
            ANOMALIE_CATEGORIE.LIBELLE,
            POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE,
            POIDS_ANOMALIE.VAL_INDISPO_HBE,
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECEPTION), true)).`as`("isReceptionAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECO_INIT), true)).`as`("isRecoInitAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.CTP), true)).`as`("isCTPAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.ROP), true)).`as`("isRopAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.NP), true)).`as`("isNPAssignable"),
            (`when`(L_PEI_ANOMALIE.ANOMALIE_ID.isNotNull, true)).`as`("isAssigned"),
        )
            .from(ANOMALIE)
            .join(PEI)
            .on(PEI.ID.eq(peiId))
            .join(POIDS_ANOMALIE)
            .on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(PEI.NATURE_ID))
            .join(ANOMALIE_CATEGORIE)
            .on(
                (ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
                    .and(!ANOMALIE_CATEGORIE.CODE.eq(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME)),
            )
            .leftJoin(L_PEI_ANOMALIE)
            .on(
                (L_PEI_ANOMALIE.PEI_ID.eq(peiId))
                    .and(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID)),
            )
            .where(isActiveCondition(isActive = true))
            .orderBy(ANOMALIE_CATEGORIE.ORDRE, ANOMALIE.ORDRE)
            .fetchInto()

    fun deleteLiaisonByPei(idPEi: UUID) = dsl.deleteFrom(L_PEI_ANOMALIE).where(L_PEI_ANOMALIE.PEI_ID.eq(idPEi)).execute()

    data class CompletedAnomalie(
        val anomalieId: UUID,
        val anomalieCode: String,
        val anomalieLibelle: String,
        val anomalieCommentaire: String?,
        val anomalieAnomalieCategorieId: UUID,
        val anomalieCategorieLibelle: String,
        val poidsAnomalieValIndispoTerrestre: Int?,
        val poidsAnomalieValIndispoHbe: Int?,
        val isReceptionAssignable: Boolean,
        val isRecoInitAssignable: Boolean,
        val isCTPAssignable: Boolean,
        val isRopAssignable: Boolean,
        val isNPAssignable: Boolean,
        val isAssigned: Boolean,
    )

    /** Remonte les anomalies assignables pour chaque pei d'une tournée selon sa nature
     * Ne remonte pas les anomalies appartenant à la catégorie 'SYSTEME'
     * Un flag pour chaque type_visite indiquent si l'anomalie peut etre assignée lors d'une visite de ce type
     * @param tourneeId : UUID
     * @return une map d'objet CompletedAnomalie sur un peiId */
    fun getAllAnomalieAssignableByPeiTourneeId(tourneeId: UUID): MutableMap<UUID?, MutableList<CompletedAnomalie>> =
        dsl.select(
            PEI.ID,
            ANOMALIE.ID,
            ANOMALIE.CODE,
            ANOMALIE.LIBELLE,
            ANOMALIE.COMMENTAIRE,
            ANOMALIE.ANOMALIE_CATEGORIE_ID,
            ANOMALIE_CATEGORIE.LIBELLE,
            POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE,
            POIDS_ANOMALIE.VAL_INDISPO_HBE,
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECEPTION), true)).`as`("isReceptionAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECO_INIT), true)).`as`("isRecoInitAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.CTP), true)).`as`("isCTPAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.ROP), true)).`as`("isRopAssignable"),
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.NP), true)).`as`("isNPAssignable"),
            (`when`(L_PEI_ANOMALIE.ANOMALIE_ID.isNotNull, true)).`as`("isAssigned"),
        )
            .from(ANOMALIE)
            .join(L_TOURNEE_PEI).on(L_TOURNEE_PEI.TOURNEE_ID.eq(tourneeId))
            .join(PEI).on(L_TOURNEE_PEI.PEI_ID.eq(PEI.ID))
            .join(POIDS_ANOMALIE)
            .on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(PEI.NATURE_ID))
            .join(ANOMALIE_CATEGORIE)
            .on(
                (ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
                    .and(ANOMALIE_CATEGORIE.CODE.ne(GlobalConstants.CATEGORIE_ANOMALIE_SYSTEME)),
            )
            .leftJoin(L_PEI_ANOMALIE)
            .on(
                (PEI.ID.eq(L_PEI_ANOMALIE.PEI_ID))
                    .and(L_PEI_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID)),
            )
            .where(isActiveCondition(isActive = true))
            .orderBy(ANOMALIE_CATEGORIE.ORDRE, ANOMALIE.ORDRE)
            .fetchGroups(PEI.ID, CompletedAnomalie::class.java)

    fun getAnomalieCategorie(): Collection<AnomalieCategorie> =
        dsl.selectFrom(ANOMALIE_CATEGORIE)
            .where(ANOMALIE_CATEGORIE.ACTIF.isTrue)
            .orderBy(ANOMALIE_CATEGORIE.ORDRE)
            .fetchInto()

    fun getAnomalieForExportCTP(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            ANOMALIE.ID.`as`("id"),
            ANOMALIE.CODE.`as`("code"),
            ANOMALIE.LIBELLE.`as`("libelle"),
        )
            .distinctOn(
                ANOMALIE.CODE,
                ANOMALIE.LIBELLE,
            )
            .from(ANOMALIE)
            .join(POIDS_ANOMALIE).on(ANOMALIE.ID.eq(POIDS_ANOMALIE.ANOMALIE_ID))
            .join(NATURE).on(POIDS_ANOMALIE.NATURE_ID.eq(NATURE.ID))
            .where(NATURE.TYPE_PEI.eq(TypePei.PIBI))
            .and(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.CTP))
            .fetchInto()

    fun getAnomalieWithNature(natureCode: String, typeVisite: TypeVisite?, typePei: TypePei, limit: Int?, offset: Int?): Collection<ApiAnomalieWithNature> =
        dsl.select(
            ANOMALIE.CODE,
            ANOMALIE.LIBELLE,
            POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE,
            POIDS_ANOMALIE.TYPE_VISITE.`as`("listTypeVisite"),
        ).from(ANOMALIE)
            .join(POIDS_ANOMALIE)
            .on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .join(NATURE)
            .on(NATURE.ID.eq(POIDS_ANOMALIE.NATURE_ID))
            .where(
                typeVisite?.let {
                    POIDS_ANOMALIE.TYPE_VISITE.contains(typeVisite)
                } ?: DSL.noCondition(),
            )
            .and(NATURE.CODE.equalIgnoreCase(natureCode))
            .and(NATURE.TYPE_PEI.eq(typePei))
            .limit(limit)
            .offset(offset)
            .fetchInto()

    /**
     * Retourne les ID de PEI associés à l'anomalie dont l'ID est passé en paramètre...
     * Permet de propager une modification du poids de l'anomalie lors de l'enregistrement.
     */
    fun getPeiIds(anomalieId: UUID): Collection<UUID> =
        dsl.select(L_PEI_ANOMALIE.PEI_ID).from(L_PEI_ANOMALIE).where(L_PEI_ANOMALIE.ANOMALIE_ID.eq(anomalieId)).fetchInto()

    fun getNbAnomaliesChecked(peiNatureId: UUID, typeVisite: TypeVisite, listControlees: Collection<String>): Int =
        dsl.selectCount()
            .from(ANOMALIE)
            .join(POIDS_ANOMALIE).on(ANOMALIE.ID.eq(POIDS_ANOMALIE.ANOMALIE_ID))
            .where(
                DSL.and(
                    POIDS_ANOMALIE.NATURE_ID.eq(peiNatureId),
                    POIDS_ANOMALIE.TYPE_VISITE.contains(typeVisite),
                    ANOMALIE.CODE.`in`(listControlees),
                ),
            )
            .fetchSingleInto()

    fun getIdsByCodes(listeCode: Collection<String>): List<UUID> =
        dsl.select(ANOMALIE.ID)
            .from(ANOMALIE)
            .where(ANOMALIE.CODE.`in`(listeCode))
            .fetchInto()
}
