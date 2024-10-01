package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.`when`
import remocra.GlobalConstants
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.db.jooq.remocra.tables.pojos.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_TOURNEE_PEI
import remocra.db.jooq.remocra.tables.references.L_VISITE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import java.util.UUID

class AnomalieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Anomalie> {

    override fun getMapById(): Map<UUID, Anomalie> = dsl.selectFrom(ANOMALIE).where(ANOMALIE.ACTIF.isTrue).fetchInto<Anomalie>().associateBy { it.anomalieId }

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
        dsl.batch(listePeiAnomalie.map { DSL.insertInto(L_PEI_ANOMALIE).set(dsl.newRecord(L_PEI_ANOMALIE, it)) })
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
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECOP), true)).`as`("isRecopAssignable"),
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
        val isRecopAssignable: Boolean,
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
            (`when`(POIDS_ANOMALIE.TYPE_VISITE.contains(TypeVisite.RECOP), true)).`as`("isRecopAssignable"),
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
            .fetchGroups(PEI.ID, CompletedAnomalie::class.java)
}
