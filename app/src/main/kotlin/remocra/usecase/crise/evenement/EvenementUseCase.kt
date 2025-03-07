package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import remocra.db.EvenementRepository
import remocra.db.jooq.remocra.enums.EvenementStatutMode
import remocra.usecase.AbstractUseCase
import java.util.UUID

class EvenementUseCase : AbstractUseCase() {
    @Inject lateinit var evenementRepository: EvenementRepository

    fun getTypeEventFromCrise(criseId: UUID, statut: EvenementStatutMode): Collection<EvenementRepository.FilterEvent> =
        evenementRepository.getTypeEventFromCrise(criseId, statut)
}
