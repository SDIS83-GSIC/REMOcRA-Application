package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import remocra.db.EvenementRepository
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.usecase.AbstractUseCase
import java.util.UUID

class EvenementUseCase
@Inject
constructor(
    private val evenementRepository: EvenementRepository,
) :
    AbstractUseCase() {

    fun getTypeEventFromCrise(criseId: UUID, statut: EvenementStatutMode): Collection<EvenementRepository.FilterEvent> =
        evenementRepository.getTypeEventFromCrise(criseId, statut)
}
