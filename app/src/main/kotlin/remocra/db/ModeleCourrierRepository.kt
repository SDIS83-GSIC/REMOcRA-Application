package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER

class ModeleCourrierRepository @Inject constructor(private val dsl: DSLContext) {

    fun getByCode(code: String): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.eq(code))
            .fetchSingleInto()
}
