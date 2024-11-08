package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.InsertSetStep
import org.jooq.Record
import remocra.data.PenaData
import remocra.db.PeiRepository.Companion.peiData
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.Pena
import remocra.db.jooq.remocra.tables.references.PENA
import java.util.UUID

class PenaRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getInfoPena(penaId: UUID): PenaData =
        dsl.select(peiData).select(
            PENA.CAPACITE,
            PENA.DISPONIBILITE_HBE,
            PENA.QUANTITE_APPOINT,
            PENA.CAPACITE_ILLIMITEE,
            PENA.CAPACITE_INCERTAINE,
            PENA.MATERIAU_ID,
        )
            .from(PEI)
            .join(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .where(PEI.ID.eq(penaId))
            .fetchSingleInto()

    fun upsertPena(pena: PenaData): Int =
        dsl.insertInto(PENA)
            .setPenaField(pena)

    /**
     * Permet d'insérer ou d'update les champs d'un PENA.
     * Le jour où un champ est ajouté, il suffira de mettre à jour cette fonction.
     */
    private fun <R : Record?> InsertSetStep<R>.setPenaField(pena: PenaData): Int {
        val record = dsl.newRecord(
            PENA,
            Pena(
                penaId = pena.peiId,
                penaCapacite = pena.penaCapacite,
                penaMateriauId = pena.penaMateriauId,
                penaCapaciteIllimitee = pena.penaCapaciteIllimitee,
                penaQuantiteAppoint = pena.penaQuantiteAppoint,
                penaDisponibiliteHbe = pena.penaDisponibiliteHbe,
                penaCapaciteIncertaine = pena.penaCapaciteIncertaine,
                penaCoordonneDfci = null, // TODO ?
            ),
        )

        return set(record).onConflict(PENA.ID)
            .doUpdate()
            .set(record)
            .execute()
    }

    fun deleteById(peiId: UUID) = dsl.deleteFrom(PENA).where(PENA.ID.eq(peiId)).execute()
}
