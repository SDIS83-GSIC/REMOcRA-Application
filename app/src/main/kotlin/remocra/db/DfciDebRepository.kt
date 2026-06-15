package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.db.jooq.remocra.tables.references.DFCI_DEB
import java.util.UUID

/**
 * Classe permettant de faire les requêtes liées aux débroussaillements sur la bd
 */
class DfciDebRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Requête permettant de récupérer un débroussaillement suivant l'id donné
     * @param dfciDebId id du débroussaillement
     * @return les données du débroussaillement
     */
    fun getDfciDebById(dfciDebId: UUID): DfciDeb =
        dsl
            .selectFrom(DFCI_DEB)
            .where(DFCI_DEB.ID.eq(dfciDebId))
            .fetchSingleInto()
}
