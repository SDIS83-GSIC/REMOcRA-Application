package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.references.VISITE
import remocra.db.jooq.remocra.tables.references.VISITE_CTRL_DEBIT_PRESSION
import java.util.UUID

class VisiteRepository
@Inject constructor(
    private val dsl: DSLContext,
) {

    fun getLastVisiteDebitPression(peiId: UUID): VisiteCtrlDebitPression? = dsl.select(*VISITE_CTRL_DEBIT_PRESSION.fields())
        .from(VISITE_CTRL_DEBIT_PRESSION)
        .innerJoin(VISITE).on(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.eq(VISITE.ID))
        .where(VISITE.PEI_ID.eq(peiId))
        .orderBy(VISITE.DATE.desc())
        .fetchOneInto()
}
