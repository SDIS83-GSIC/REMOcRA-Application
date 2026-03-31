package remocra.usecase.nature

import jakarta.inject.Inject
import remocra.db.NatureRepository
import remocra.usecase.AbstractUseCase

class NatureUseCase
@Inject
constructor(
    private val natureRepository: NatureRepository,
) :
    AbstractUseCase() {
    fun getNatureForSelect(): List<NatureRepository.IdLibelleNature> = natureRepository.getNatureForSelect()
}
