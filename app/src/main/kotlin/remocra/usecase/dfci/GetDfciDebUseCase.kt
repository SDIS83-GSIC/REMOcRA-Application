package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.db.DfciDebRepository
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetDfciDebUseCase @Inject constructor(private val dfciDebRepository: DfciDebRepository) : AbstractUseCase() {

    /**
     * Exécute la récupération d'un débroussaillement
     */
    fun execute(dfciDebId: UUID): DfciDeb =
        dfciDebRepository.getDfciDebById(dfciDebId)
}
