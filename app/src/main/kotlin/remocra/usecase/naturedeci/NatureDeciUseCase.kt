package remocra.usecase.naturedeci

import jakarta.inject.Inject
import remocra.db.NatureDeciRepository
import remocra.usecase.AbstractUseCase

class NatureDeciUseCase : AbstractUseCase() {
    @Inject
    lateinit var natureDeciRepository: NatureDeciRepository
    fun getNatureDeciForSelect(): List<NatureDeciRepository.IdLibelleNatureDeci> = natureDeciRepository.getNatureDeciForSelect()
}
