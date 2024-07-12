package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Visite
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
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
) {

    fun getLastVisite(peiId: UUID): Visite? = dsl.selectFrom(VISITE)
        .where(VISITE.PEI_ID.eq(peiId))
        .orderBy(VISITE.DATE.desc())
        .fetchAnyInto()

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
        .fetchOneInto()

    fun getAllVisiteByPeiId(peiId: UUID): List<VisiteComplete> =
        dsl.select(VISITE.fields().toList())
            .from(VISITE)
            .where(VISITE.PEI_ID.eq(peiId))
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

    fun getAllCtrlByListVisiteId(listVisiteId: List<UUID>): List<VisiteCtrlDebitPression> =
        dsl.selectFrom(VISITE_CTRL_DEBIT_PRESSION)
            .where(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.`in`(listVisiteId))
            .fetchInto()

    // CreateVisiteUseCase
    fun insertVisite(visite: Visite) =
        dsl.insertInto(VISITE)
            .set(dsl.newRecord(VISITE, visite))
            .execute()

    fun insertCDP(ctrlDebitPression: VisiteCtrlDebitPression) =
        dsl.insertInto(VISITE_CTRL_DEBIT_PRESSION)
            .set(dsl.newRecord(VISITE_CTRL_DEBIT_PRESSION, ctrlDebitPression))
            .execute()
}
