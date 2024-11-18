package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import java.util.UUID

class CourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getDocumentByCourrier(courrierId: UUID): Document =
        dsl.select(*DOCUMENT.fields())
            .from(COURRIER)
            .join(DOCUMENT)
            .on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(COURRIER.ID.eq(courrierId))
            .fetchSingleInto()
}
