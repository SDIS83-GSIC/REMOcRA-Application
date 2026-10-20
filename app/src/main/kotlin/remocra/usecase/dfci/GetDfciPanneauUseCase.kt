package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.db.DfciPanneauRepository
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Use case pour récupérer un panneau suivant son id
 */
class GetDfciPanneauUseCase @Inject constructor(private val dfciPanneauRepository: DfciPanneauRepository) : AbstractUseCase() {

    /**
     * Exécute la requête permettant de récupérer un panneau suivant l'id
     */
    fun execute(dfciPanneauId: UUID): DfciPanneau =
        dfciPanneauRepository.getDfciPanneauById(dfciPanneauId)
}
