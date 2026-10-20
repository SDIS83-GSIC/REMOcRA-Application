package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.db.DfciPistesRepository
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Classe UseCase permettant de récupérer une piste selon son id
 */
class GetDfciPisteUseCase @Inject constructor(private val dfciPistesRepository: DfciPistesRepository) : AbstractUseCase() {

    /**
     * Exécute la récupération de la piste selon l'id donné
     * @param pisteId de la piste à récupérer
     */
    fun execute(pisteId: UUID): DfciPiste = dfciPistesRepository.getDfciPistebyId(pisteId)
}
