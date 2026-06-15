package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.db.jooq.remocra.tables.references.DFCI_PANNEAU
import java.util.UUID

/**
 * Repository permettant d'effectuer les requêtes sur la table dfci_panneau
 */
class DfciPanneauRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Requête permettant de récupérer un panneau selon son id
     * @param dfciPanneauId id du panneau
     */
    fun getDfciPanneauById(dfciPanneauId: UUID): DfciPanneau =
        dsl.selectFrom(DFCI_PANNEAU)
            .where(DFCI_PANNEAU.ID.eq(dfciPanneauId))
            .fetchSingleInto()
}
