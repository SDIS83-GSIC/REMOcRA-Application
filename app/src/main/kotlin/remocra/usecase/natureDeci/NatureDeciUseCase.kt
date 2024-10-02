package remocra.usecase.natureDeci

import com.google.inject.Inject
import remocra.db.NatureDeciRepository

class NatureDeciUseCase {
    @Inject
    lateinit var natureDeciRepository: NatureDeciRepository
    fun getNatureDeciForSelect(): List<NatureDeciRepository.IdLibelleNatureDeci> = natureDeciRepository.getNatureDeciForSelect()
}
