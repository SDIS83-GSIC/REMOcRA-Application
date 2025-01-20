package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.PeiPrescrit
import remocra.db.jooq.remocra.tables.references.PEI_PRESCRIT
import java.util.UUID

class PeiPrescritRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun insertPeiPrescrit(peiPrescrit: PeiPrescrit) =
        dsl.insertInto(PEI_PRESCRIT).set(dsl.newRecord(PEI_PRESCRIT, peiPrescrit)).execute()

    fun updatePeiPrescrit(peiPrescrit: PeiPrescrit) =
        dsl.update(PEI_PRESCRIT)
            .set(PEI_PRESCRIT.NB_POTEAUX, peiPrescrit.peiPrescritNbPoteaux)
            .set(PEI_PRESCRIT.DEBIT, peiPrescrit.peiPrescritDebit)
            .set(PEI_PRESCRIT.DATE, peiPrescrit.peiPrescritDate)
            .set(PEI_PRESCRIT.AGENT, peiPrescrit.peiPrescritAgent)
            .set(PEI_PRESCRIT.NUM_DOSSIER, peiPrescrit.peiPrescritNumDossier)
            .set(PEI_PRESCRIT.COMMENTAIRE, peiPrescrit.peiPrescritCommentaire)
            .set(PEI_PRESCRIT.ORGANISME_ID, peiPrescrit.peiPrescritOrganismeId)
            .where(PEI_PRESCRIT.ID.eq(peiPrescrit.peiPrescritId))
            .execute()

    fun getById(peiPrescritid: UUID): PeiPrescrit =
        dsl.selectFrom(PEI_PRESCRIT).where(PEI_PRESCRIT.ID.eq(peiPrescritid)).fetchSingleInto()

    fun deleteById(peiPrescritid: UUID) =
        dsl.deleteFrom(PEI_PRESCRIT).where(PEI_PRESCRIT.ID.eq(peiPrescritid)).execute()
}
