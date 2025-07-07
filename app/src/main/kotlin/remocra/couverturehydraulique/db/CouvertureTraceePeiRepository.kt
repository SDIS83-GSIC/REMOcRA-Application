package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.data.enums.CodeSdis
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.jooq.couverturehydraulique.tables.pojos.CouvertureTraceePei
import remocra.db.jooq.couverturehydraulique.tables.references.COUVERTURE_TRACEE_PEI
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PIBI
import java.util.UUID

/**
 * Repository pour les données de couverture tracée PEI
 */
class CouvertureTraceePeiRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Récupère toutes les couvertures PEI pour une distance et étude
     */
    fun getByDistanceAndEtude(distance: Int, idEtude: UUID): List<CouvertureTraceePei> =
        dsl.selectFrom(COUVERTURE_TRACEE_PEI)
            .where(COUVERTURE_TRACEE_PEI.DISTANCE.eq(distance))
            .and(COUVERTURE_TRACEE_PEI.ETUDE_ID.eq(idEtude))
            .fetchInto()

    /**
     * Récupère les couvertures PEI non gros débit
     */
    fun getCouverturesNonGrosDebit(distance: Int, idEtude: UUID, codeSdis: CodeSdis): List<CouvertureTraceePei> {
        return dsl.selectFrom(COUVERTURE_TRACEE_PEI)
            .where(COUVERTURE_TRACEE_PEI.DISTANCE.eq(distance))
            .and(COUVERTURE_TRACEE_PEI.ETUDE_ID.eq(idEtude))
            .and(isNotGrosDebit(COUVERTURE_TRACEE_PEI.ID, codeSdis))
            .fetchInto()
    }

    /**
     * Récupère les couvertures PEI gros débit
     */
    fun getCouverturesGrosDebit(distance: Int, idEtude: UUID, codeSdis: CodeSdis): List<CouvertureTraceePei> {
        return dsl.selectFrom(COUVERTURE_TRACEE_PEI)
            .where(COUVERTURE_TRACEE_PEI.DISTANCE.eq(distance))
            .and(COUVERTURE_TRACEE_PEI.ETUDE_ID.eq(idEtude))
            .and(isGrosDebit(COUVERTURE_TRACEE_PEI.ID, codeSdis))
            .fetchInto()
    }

    /**
     * Supprime une couverture PEI
     */
    fun delete(distance: Int, peiId: UUID, idEtude: UUID) {
        dsl.deleteFrom(COUVERTURE_TRACEE_PEI)
            .where(COUVERTURE_TRACEE_PEI.DISTANCE.eq(distance))
            .and(COUVERTURE_TRACEE_PEI.ID.eq(peiId))
            .and(COUVERTURE_TRACEE_PEI.ETUDE_ID.eq(idEtude))
            .execute()
    }

    /**
     * Insère une nouvelle couverture PEI
     */
    fun insert(distance: Int, peiId: UUID, idEtude: UUID, geometrie: Geometry) {
        dsl.insertInto(COUVERTURE_TRACEE_PEI)
            .set(COUVERTURE_TRACEE_PEI.DISTANCE, distance)
            .set(COUVERTURE_TRACEE_PEI.ID, peiId)
            .set(COUVERTURE_TRACEE_PEI.ETUDE_ID, idEtude)
            .set(COUVERTURE_TRACEE_PEI.GEOMETRIE, geometrie)
            .execute()
    }

    /**
     * Logique métier pour déterminer si un PEI est "gros débit" (JOOQ pur avec jointures)
     * - Pour BSPP :
     *   - PEI existant : PI + DIAM150 OU BI + DIAM100 + jumelé
     *   - PEI projet : DIAM150 + canalisation >= 150
     * - Sinon : aucun PEI n'est gros débit
     */
    fun isGrosDebit(peiidField: TableField<Record, UUID?>, codesdis: CodeSdis): Condition {
        return when (codesdis) {
            CodeSdis.BSPP -> {
                // PEI existant : PI + DIAM150
                val existsPiDiam150 = DSL.exists(
                    DSL.selectOne()
                        .from(PEI)
                        .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
                        .join(PIBI).on(PIBI.ID.eq(PEI.ID))
                        .join(DIAMETRE).on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
                        .where(PEI.ID.eq(peiidField))
                        .and(NATURE.CODE.eq(GlobalConstants.NATURE_PI))
                        .and(DIAMETRE.CODE.eq(GlobalConstants.DIAMETRE_150)),
                )
                // PEI existant : BI + DIAM100 + jumelé
                val existsBiDiam100Jumele = DSL.exists(
                    DSL.selectOne()
                        .from(PEI)
                        .join(NATURE).on(PEI.NATURE_ID.eq(NATURE.ID))
                        .join(PIBI).on(PIBI.ID.eq(PEI.ID))
                        .join(DIAMETRE).on(PIBI.DIAMETRE_ID.eq(DIAMETRE.ID))
                        .where(PEI.ID.eq(peiidField))
                        .and(NATURE.CODE.eq(GlobalConstants.NATURE_BI))
                        .and(DIAMETRE.CODE.eq(GlobalConstants.DIAMETRE_100))
                        .and(PIBI.JUMELE_ID.isNotNull),
                )
                // PEI projet : DIAM150 + canalisation >= 150
                val existsProjetDiam150Canal150 = DSL.exists(
                    DSL.selectOne()
                        .from(PEI_PROJET)
                        .join(DIAMETRE).on(PEI_PROJET.DIAMETRE_ID.eq(DIAMETRE.ID))
                        .where(PEI_PROJET.ID.eq(peiidField))
                        .and(DIAMETRE.CODE.eq(GlobalConstants.DIAMETRE_150))
                        .and(PEI_PROJET.DIAMETRE_CANALISATION.ge(150)),
                )
                existsPiDiam150.or(existsBiDiam100Jumele).or(existsProjetDiam150Canal150)
            }
            // Ajouter d'autres cas ici si besoin
            else -> DSL.falseCondition()
        }
    }

    private fun isNotGrosDebit(peiidField: TableField<Record, UUID?>, codesdis: CodeSdis): Condition {
        return isGrosDebit(peiidField, codesdis).not()
    }
}
