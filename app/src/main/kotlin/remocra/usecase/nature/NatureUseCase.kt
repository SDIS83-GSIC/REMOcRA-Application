package remocra.usecase.nature

import com.google.inject.Inject
import remocra.db.NatureRepository
import remocra.usecase.AbstractUseCase

class NatureUseCase : AbstractUseCase() {
    @Inject
    lateinit var natureRepository: NatureRepository
    fun getNatureForSelect(): List<NatureRepository.IdLibelleNature> = natureRepository.getNatureForSelect()
}
