package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.ApiVisiteData
import remocra.data.ApiVisiteSpecifiqueData
import remocra.data.CreationVisiteCtrl
import remocra.data.VisiteData
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.pojos.Visite
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_VISITE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.POIDS_ANOMALIE
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VISITE_CTRL_DEBIT_PRESSION
import java.time.ZonedDateTime
import java.util.UUID

class VisiteRepository
@Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getLastVisite(peiId: UUID): Visite? = dsl.selectFrom(VISITE)
        .where(VISITE.PEI_ID.eq(peiId))
        .orderBy(VISITE.DATE.desc())
        .fetchAnyInto()

    fun getAvantDerniereVisite(peiId: UUID, visiteId: UUID): UUID =
        dsl.select(VISITE.ID).from(VISITE)
            .where(VISITE.PEI_ID.eq(peiId))
            .and(VISITE.ID.ne(visiteId))
            .orderBy(VISITE.DATE.desc())
            .limit(1)
            .fetchSingleInto()

    fun getLVisiteAnomalie(visiteId: UUID): Collection<UUID> {
        return dsl
            .select(L_VISITE_ANOMALIE.ANOMALIE_ID)
            .from(L_VISITE_ANOMALIE)
            .where(L_VISITE_ANOMALIE.VISITE_ID.eq(visiteId))
            .fetchInto()
    }

    fun getLastVisiteBefore(peiId: UUID, instant: ZonedDateTime): Visite {
        return dsl
            .selectFrom(VISITE)
            .where(VISITE.PEI_ID.eq(peiId))
            .and(VISITE.DATE.lessThan(instant))
            .orderBy(VISITE.DATE.desc())
            .limit(1)
            .fetchSingleInto()
    }

    fun getAnomaliesFromVisite(visiteId: UUID): Collection<UUID> = dsl
        .select(L_VISITE_ANOMALIE.ANOMALIE_ID)
        .from(L_VISITE_ANOMALIE)
        .where(L_VISITE_ANOMALIE.VISITE_ID.eq(visiteId))
        .fetchInto()

    fun getLastVisiteDebitPression(peiId: UUID): VisiteCtrlDebitPression? = dsl.select(*VISITE_CTRL_DEBIT_PRESSION.fields())
        .from(VISITE_CTRL_DEBIT_PRESSION)
        .innerJoin(VISITE).on(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(VISITE.ID))
        .where(VISITE.PEI_ID.eq(peiId))
        .orderBy(VISITE.DATE.desc())
        .fetchAnyInto()

    fun getLastVisiteDebitPression(listePeiId: List<UUID>): List<VisiteCtrlDebitPression> = dsl.select(*VISITE_CTRL_DEBIT_PRESSION.fields())
        .from(VISITE_CTRL_DEBIT_PRESSION)
        .innerJoin(VISITE).on(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(VISITE.ID))
        .where(VISITE.PEI_ID.`in`(listePeiId))
        .orderBy(VISITE.DATE.desc())
        .fetchInto()

    fun getVisiteCompleteByVisiteId(visiteId: UUID): VisiteComplete =
        dsl.select(VISITE.fields().toList())
            .from(VISITE)
            .where(VISITE.ID.eq(visiteId))
            .fetchSingle()
            .map { record ->
                VisiteComplete(
                    visiteId = record.getValue(VISITE.ID)!!,
                    visitePeiId = record.getValue(VISITE.PEI_ID)!!,
                    visiteDate = record.getValue(VISITE.DATE)!!,
                    visiteTypeVisite = record.getValue(VISITE.TYPE_VISITE)!!,
                    visiteAgent1 = record.getValue(VISITE.AGENT1),
                    visiteAgent2 = record.getValue(VISITE.AGENT2),
                    visiteObservation = record.getValue(VISITE.OBSERVATION),
                    listeAnomalie = null,
                    ctrlDebitPression = null,
                )
            }

    fun getAllVisiteByPeiId(peiId: UUID): List<VisiteComplete> =
        getAllVisiteByPeiId(listOf(peiId))

    fun getAllVisiteByPeiId(listePeiId: List<UUID>): List<VisiteComplete> =
        dsl.select(VISITE.fields().toList())
            .from(VISITE)
            .where(VISITE.PEI_ID.`in`(listePeiId))
            .orderBy(VISITE.DATE.desc())
            .fetch()
            .map { record ->
                VisiteComplete(
                    visiteId = record.getValue(VISITE.ID)!!,
                    visitePeiId = record.getValue(VISITE.PEI_ID)!!,
                    visiteDate = record.getValue(VISITE.DATE)!!,
                    visiteTypeVisite = record.getValue(VISITE.TYPE_VISITE)!!,
                    visiteAgent1 = record.getValue(VISITE.AGENT1),
                    visiteAgent2 = record.getValue(VISITE.AGENT2),
                    visiteObservation = record.getValue(VISITE.OBSERVATION),
                    listeAnomalie = null,
                    ctrlDebitPression = null,
                )
            }

    data class VisiteComplete(
        val visiteId: UUID,
        val visitePeiId: UUID,
        val visiteDate: ZonedDateTime,
        val visiteTypeVisite: TypeVisite,
        val visiteAgent1: String?,
        val visiteAgent2: String?,
        val visiteObservation: String?,
        var listeAnomalie: MutableList<CompletedAnomalie>?,
        var ctrlDebitPression: VisiteCtrlDebitPression?,
    )

    fun getCompletedAnomalieByPeiId(peiUUID: UUID): MutableMap<UUID?, MutableList<CompletedAnomalie>?> =
        dsl.select(
            ANOMALIE.ID,
            ANOMALIE.CODE,
            ANOMALIE.LIBELLE,
            ANOMALIE.COMMENTAIRE,
            ANOMALIE_CATEGORIE.ID,
            ANOMALIE_CATEGORIE.LIBELLE,
            POIDS_ANOMALIE.VAL_INDISPO_TERRESTRE,
            POIDS_ANOMALIE.VAL_INDISPO_HBE,
            L_VISITE_ANOMALIE.VISITE_ID,
        )
            .from(ANOMALIE)
            .join(ANOMALIE_CATEGORIE).on(ANOMALIE_CATEGORIE.ID.eq(ANOMALIE.ANOMALIE_CATEGORIE_ID))
            .join(L_VISITE_ANOMALIE).on(L_VISITE_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .join(VISITE).on(VISITE.ID.eq(L_VISITE_ANOMALIE.VISITE_ID))
            .join(PEI).on(PEI.ID.eq(peiUUID))
            .join(POIDS_ANOMALIE)
            .on(POIDS_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
            .and(POIDS_ANOMALIE.NATURE_ID.eq(PEI.NATURE_ID))
            .orderBy(ANOMALIE_CATEGORIE.ORDRE, ANOMALIE.ORDRE)
            .fetchGroups(L_VISITE_ANOMALIE.VISITE_ID, CompletedAnomalie::class.java)

    data class CompletedAnomalie(
        val anomalieId: UUID,
        val anomalieCode: String,
        val anomalieLibelle: String,
        val anomalieCommentaire: String?,
        val anomalieCategorieId: UUID,
        val anomalieCategorieLibelle: String,
        val poidsAnomalieValIndispoTerrestre: Int?,
        val poidsAnomalieValIndispoHbe: Int?,
    )

    fun getCtrlByVisiteId(visiteId: UUID): VisiteCtrlDebitPression? =
        dsl.selectFrom(VISITE_CTRL_DEBIT_PRESSION)
            .where(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(visiteId))
            .fetchOneInto()

    fun getAllCtrlByListVisiteId(listVisiteId: List<UUID>): List<VisiteCtrlDebitPression> =
        dsl.selectFrom(VISITE_CTRL_DEBIT_PRESSION)
            .where(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.`in`(listVisiteId))
            .fetchInto()

    fun visiteAlreadyExists(peiId: UUID, visiteType: TypeVisite): Boolean =
        dsl.fetchExists(
            VISITE,
            VISITE.PEI_ID.eq(peiId)
                .and(VISITE.TYPE_VISITE.eq(visiteType)),
        )
    fun getCountVisite(peiId: UUID): Int = dsl.fetchCount(VISITE, VISITE.PEI_ID.eq(peiId))

    // CreateVisiteUseCase
    fun insertVisite(visite: Visite) =
        dsl.insertInto(VISITE)
            .set(dsl.newRecord(VISITE, visite))
            .execute()

    fun insertCDP(ctrlDebitPression: VisiteCtrlDebitPression) =
        dsl.insertInto(VISITE_CTRL_DEBIT_PRESSION)
            .set(dsl.newRecord(VISITE_CTRL_DEBIT_PRESSION, ctrlDebitPression))
            .execute()

    // DeleteVisiteUseCase
    fun getLastPeiVisiteId(peiId: UUID): UUID? =
        dsl.select(VISITE.ID)
            .from(VISITE)
            .where(VISITE.PEI_ID.eq(peiId))
            .orderBy(VISITE.DATE.desc())
            .fetchAnyInto()

    fun deleteAllVisiteAnomalies(visiteId: UUID) =
        dsl.deleteFrom(L_VISITE_ANOMALIE)
            .where(L_VISITE_ANOMALIE.VISITE_ID.eq(visiteId))
            .execute()

    fun deleteVisiteCtrl(visiteId: UUID) =
        dsl.deleteFrom(VISITE_CTRL_DEBIT_PRESSION)
            .where(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(visiteId))
            .execute()

    fun deleteVisite(visiteId: UUID) =
        dsl.deleteFrom(VISITE)
            .where(VISITE.ID.eq(visiteId))
            .execute()

    fun getAllForApi(numeroComplet: String, typeVisite: TypeVisite?, moment: ZonedDateTime?, derniereOnly: Boolean, limit: Int?, offset: Int?): Collection<ApiVisiteData> {
        return dsl.select(VISITE.ID, VISITE.DATE.`as`("moment"), VISITE.TYPE_VISITE)
            .select(
                DSL.multiset(
                    DSL.selectDistinct(ANOMALIE.CODE)
                        .from(L_VISITE_ANOMALIE)
                        .innerJoin(ANOMALIE).on(L_VISITE_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                        .where(L_PEI_ANOMALIE.PEI_ID.eq(Pei.PEI.ID)),
                ).`as`("anomalies"),
            )
            .from(VISITE)
            .innerJoin(PEI).on(VISITE.PEI_ID.eq(PEI.ID))
            .where(PEI.NUMERO_COMPLET.eq(numeroComplet))
            .and(typeVisite?.let { VISITE.TYPE_VISITE.eq(typeVisite) })
            .and(moment?.let { VISITE.DATE.ge(moment) })
            .limit(if (derniereOnly) 1 else (if (limit == null || limit < 0) null else limit))
            .offset(if (derniereOnly || offset == null || offset < 0) 0 else offset)
            .fetchInto()
    }

    fun getVisiteForApi(visiteId: UUID): ApiVisiteSpecifiqueData {
        return dsl.select(VISITE.ID, VISITE.DATE.`as`("moment"), VISITE.TYPE_VISITE, VISITE.AGENT1, VISITE.AGENT2, VISITE.OBSERVATION.`as`("observations"))
            .select(VISITE_CTRL_DEBIT_PRESSION.DEBIT, VISITE_CTRL_DEBIT_PRESSION.PRESSION, VISITE_CTRL_DEBIT_PRESSION.PRESSION_DYN)
            .select(
                DSL.multiset(
                    DSL.selectDistinct(ANOMALIE.CODE)
                        .from(L_VISITE_ANOMALIE)
                        .innerJoin(ANOMALIE).on(L_VISITE_ANOMALIE.ANOMALIE_ID.eq(ANOMALIE.ID))
                        .where(L_PEI_ANOMALIE.PEI_ID.eq(Pei.PEI.ID)),
                ).`as`("anomaliesConstatees"),
            )
            .from(VISITE)
            .innerJoin(PEI).on(VISITE.PEI_ID.eq(PEI.ID))
            .leftJoin(VISITE_CTRL_DEBIT_PRESSION).on(VISITE.ID.eq(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID))
            .where(VISITE.ID.eq(visiteId))
            .fetchSingleInto()

        // TODO  Anomalies contrôlées => Anomalies présentes à cette visite + Anomalies présentes à la
        //       visite précédente non présentes à cette visite
    }

    fun getAllVisiteByIdPei(peiId: UUID): List<VisiteData> =
        getAllVisiteByIdPei(listOf(peiId))

    fun getAllVisiteByIdPei(listePeiId: List<UUID>): List<VisiteData> = getAllVisiteByPeiId(listePeiId)
        .map { record ->
            VisiteData(
                visiteId = record.visiteId,
                visitePeiId = record.visitePeiId,
                visiteDate = record.visiteDate,
                visiteTypeVisite = record.visiteTypeVisite,
                visiteAgent1 = record.visiteAgent1,
                visiteAgent2 = record.visiteAgent2,
                visiteObservation = record.visiteObservation,
                listeAnomalie = record.listeAnomalie?.map { it.anomalieId } ?: emptyList(),
                isCtrlDebitPression = record.ctrlDebitPression != null,
                ctrlDebitPression = CreationVisiteCtrl(
                    ctrlDebit = record.ctrlDebitPression?.visiteCtrlDebitPressionDebit,
                    ctrlPression = record.ctrlDebitPression?.visiteCtrlDebitPressionPression,
                    ctrlPressionDyn = record.ctrlDebitPression?.visiteCtrlDebitPressionPressionDyn,
                ),
            )
        }

    fun getNbVisitesCtrlAfter(idPei: UUID, instant: ZonedDateTime): Int {
        return dsl
            .selectCount()
            .from(VISITE)
            .where(
                VISITE.PEI_ID.eq(idPei)
                    .and(VISITE.TYPE_VISITE.eq(TypeVisite.CTP))
                    .and(VISITE.DATE.gt(instant)),
            )
            .fetchSingleInto()
    }

    /**
     * Retourne le nombre de visites de contrôle effectuées à un instant donné pour le PEI
     *
     * @param idPei UUID
     * @param instant Instant
     * @return int
     */
    fun getNbVisitesEqInstant(idPei: UUID, instant: ZonedDateTime): Int {
        return dsl
            .selectCount()
            .from(VISITE)
            .where(VISITE.PEI_ID.eq(idPei))
            .and(VISITE.DATE.eq(instant))
            .fetchSingleInto()
    }
}
