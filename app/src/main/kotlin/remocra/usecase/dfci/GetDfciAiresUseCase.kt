package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.db.DfciAiresRepository
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Classe UseCase pour récupérer l'aire via son id
 */
class GetDfciAiresUseCase @Inject constructor(private val dfciAiresRepository: DfciAiresRepository) : AbstractUseCase() {

    /**
     * Execute la récupération de l'aire
     * @param dfciAireId de l'aire voulant être récupéré
     */
    fun execute(dfciAireId: UUID): DfciAire = dfciAiresRepository.getDfciAireByID(dfciAireId)
}
