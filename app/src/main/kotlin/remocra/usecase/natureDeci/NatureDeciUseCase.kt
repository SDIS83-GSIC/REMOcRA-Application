package remocra.usecase.natureDeci

import com.google.inject.Inject
import remocra.db.NatureDeciRepository
import remocra.usecase.AbstractUseCase

class NatureDeciUseCase : AbstractUseCase() {
    @Inject
    lateinit var natureDeciRepository: NatureDeciRepository
    fun getNatureDeciForSelect(): List<NatureDeciRepository.IdLibelleNatureDeci> = natureDeciRepository.getNatureDeciForSelect()
}
