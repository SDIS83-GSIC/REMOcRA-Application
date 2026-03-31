package remocra.usecase.naturedeci

import jakarta.inject.Inject
import remocra.db.NatureDeciRepository
import remocra.usecase.AbstractUseCase

class NatureDeciUseCase
@Inject
constructor(
    private val natureDeciRepository: NatureDeciRepository,
) :
    AbstractUseCase() {
    fun getNatureDeciForSelect(): List<NatureDeciRepository.IdLibelleNatureDeci> = natureDeciRepository.getNatureDeciForSelect()
}
