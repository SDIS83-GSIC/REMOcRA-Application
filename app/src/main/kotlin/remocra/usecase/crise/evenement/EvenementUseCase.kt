package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import remocra.db.EvenementRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class EvenementUseCase : AbstractUseCase() {
    @Inject lateinit var evenementRepository: EvenementRepository

    fun getTypeEventFromCrise(criseId: UUID): Collection<EvenementRepository.FilterEvent> =
        evenementRepository.getTypeEventFromCrise(criseId)
}
