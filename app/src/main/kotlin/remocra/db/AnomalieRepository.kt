package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.LPeiAnomalie
import remocra.db.jooq.remocra.tables.pojos.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_PEI_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_VISITE_ANOMALIE
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
}
