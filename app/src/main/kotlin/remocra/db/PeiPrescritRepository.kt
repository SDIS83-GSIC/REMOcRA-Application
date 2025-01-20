package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.PeiPrescrit
import remocra.db.jooq.remocra.tables.references.PEI_PRESCRIT

class PeiPrescritRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun insertPeiPrescrit(peiPrescrit: PeiPrescrit) =
        dsl.insertInto(PEI_PRESCRIT).set(dsl.newRecord(PEI_PRESCRIT, peiPrescrit)).execute()
}
