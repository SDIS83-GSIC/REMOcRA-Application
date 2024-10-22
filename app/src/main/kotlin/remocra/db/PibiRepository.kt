package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.InsertSetStep
import org.jooq.Record
import org.jooq.impl.DSL
import remocra.GlobalConstants
import remocra.data.GlobalData
import remocra.data.PibiData
import remocra.db.PeiRepository.Companion.peiData
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.Pibi
import remocra.db.jooq.remocra.tables.references.MARQUE_PIBI
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import remocra.db.jooq.remocra.tables.references.NATURE
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VISITE_CTRL_DEBIT_PRESSION
import java.time.ZonedDateTime
import java.util.UUID

class PibiRepository @Inject constructor(
    private val dsl: DSLContext,
) {

    fun getInfoPibi(pibiId: UUID): PibiData =
        dsl.select(peiData).select(
            // DONNEE PIBI
            PIBI.DIAMETRE_ID,
            PIBI.SERVICE_EAU_ID,
            PIBI.NUMERO_SCP,
            PIBI.RENVERSABLE,
            PIBI.DISPOSITIF_INVIOLABILITE,
            PIBI.MODELE_PIBI_ID.`as`("pibiModeleId"),
            PIBI.MARQUE_PIBI_ID.`as`("pibiMarqueId"),
            PIBI.RESERVOIR_ID,
            PIBI.DEBIT_RENFORCE,
            PIBI.TYPE_CANALISATION_ID,
            PIBI.TYPE_RESEAU_ID,
            PIBI.DIAMETRE_CANALISATION,
            PIBI.SURPRESSE,
            PIBI.ADDITIVE,
            PIBI.JUMELE_ID,
        )
            .from(PEI)
            .join(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .leftJoin(MODELE_PIBI)
            .on(MODELE_PIBI.ID.eq(PIBI.MODELE_PIBI_ID))
            .leftJoin(MARQUE_PIBI)
            .on(MARQUE_PIBI.ID.eq(MODELE_PIBI.MARQUE_ID))
            .where(PEI.ID.eq(pibiId))
            .fetchSingleInto()

    fun upsertPibi(pibi: PibiData): Int =
        dsl.insertInto(PIBI).setPibiField(pibi)

    /**
     * Permet d'insérer ou d'update les champs d'un PIBI.
     * Le jour où un champ est ajouté, il suffira de mettre à jour cette fonction.
     */
    private fun <R : Record?> InsertSetStep<R>.setPibiField(pibi: PibiData): Int {
        val record = dsl.newRecord(
            PIBI,
            Pibi(
                pibiId = pibi.peiId,
                pibiSurpresse = pibi.pibiSurpresse,
                pibiAdditive = pibi.pibiAdditive,
                pibiDiametreId = pibi.pibiDiametreId,
                pibiNumeroScp = pibi.pibiNumeroScp,
                pibiReservoirId = pibi.pibiReservoirId,
                pibiRenversable = pibi.pibiRenversable,
                pibiTypeReseauId = pibi.pibiTypeReseauId,
                pibiServiceEauId = pibi.pibiServiceEauId,
                pibiDebitRenforce = pibi.pibiDebitRenforce,
                pibiTypeCanalisationId = pibi.pibiTypeCanalisationId,
                pibiDiametreCanalisation = pibi.pibiDiametreCanalisation,
                pibiDispositifInviolabilite = pibi.pibiDispositifInviolabilite,
                pibiMarquePibiId = pibi.pibiMarqueId.takeIf { pibi.pibiModeleId == null },
                pibiModelePibiId = pibi.pibiModeleId,
                pibiJumeleId = pibi.pibiJumeleId,
                pibiPenaId = null, // TODO
            ),
        )

        return set(record).onConflict(PIBI.ID)
            .doUpdate()
            .set(record)
            .execute()
    }

    /**
     * Retourne les BI qui sont à moins de DISTANCE_MAXIMALE_JUMELAGE
     * Permettra de remplir la liste déroulante pour la modification  / création d'un PEI
     * @param idPei : id du PEI en train d'être modifé
     * @param geometrie : géométrie de PEI en train d'être modifié
     * @param srid : srid de la géométrie => doit correspondre au paramètre dans la base de données
     */
    fun getBiCanJumele(coordoneeX: String, coordoneeY: String, peiId: UUID?, srid: Int): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(PEI.ID.`as`("id"), PEI.NUMERO_COMPLET.`as`("code"), PEI.NUMERO_COMPLET.`as`("libelle"))
            .from(PEI)
            .join(NATURE)
            .on(NATURE.ID.eq(PEI.NATURE_ID))
            .join(PIBI)
            .on(PIBI.ID.eq(PEI.ID))
            .where(NATURE.CODE.eq(GlobalConstants.NATURE_BI))
            .and(
                "ST_DISTANCE(${PEI.GEOMETRIE}, 'SRID=$srid;POINT($coordoneeX $coordoneeY)')" +
                    " < ${GlobalConstants.DISTANCE_MAXIMALE_JUMELAGE}",
            )
            .and(DSL.and(PIBI.JUMELE_ID.isNull).or(PIBI.JUMELE_ID.eq(peiId)))
            .and(if (peiId != null) PIBI.ID.notEqual(peiId) else DSL.trueCondition())
            .fetchInto()

    /**
     * Le jumelage se fait sur les 2 sens : Si A est jumelé avec B alors on doit mettre à jour A et B
     */
    fun updateJumelage(peiJumeleId: UUID, peiAMettreAJour: UUID) {
        dsl.update(PIBI)
            .set(PIBI.JUMELE_ID, peiJumeleId)
            .where(PIBI.ID.eq(peiAMettreAJour))
            .execute()
    }

    /**
     * Supprime les jumelages d'un PIBI
     */
    fun removeJumelage(peiId: UUID) {
        dsl.update(PIBI)
            .setNull(PIBI.JUMELE_ID)
            .where(PIBI.JUMELE_ID.eq(peiId))
            .execute()
    }

    fun deleteById(peiId: UUID) = dsl.deleteFrom(PIBI).where(PIBI.ID.eq(peiId)).execute()

    fun getHistorique(pibiId: UUID, nbHistorique: Int, listeTypeVisiteCdp: List<TypeVisite>) =
        dsl.select(
            VISITE.DATE,
            VISITE_CTRL_DEBIT_PRESSION.DEBIT.`as`("debit"),
            VISITE_CTRL_DEBIT_PRESSION.PRESSION.`as`("pression"),
            VISITE_CTRL_DEBIT_PRESSION.PRESSION_DYN.`as`("pressionDyn"),
        )
            .from(VISITE)
            .join(VISITE_CTRL_DEBIT_PRESSION)
            .on(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(VISITE.ID))
            .where(VISITE.PEI_ID.eq(pibiId))
            .and(VISITE.TYPE_VISITE.`in`(listeTypeVisiteCdp))
            .orderBy(VISITE.DATE.desc())
            .limit(nbHistorique)
            .fetchInto<DebitPressionChart>()

    data class DebitPressionChart(
        val visiteDate: ZonedDateTime,
        val debit: Int?,
        val pression: Double?,
        val pressionDyn: Double?,
    )
}
