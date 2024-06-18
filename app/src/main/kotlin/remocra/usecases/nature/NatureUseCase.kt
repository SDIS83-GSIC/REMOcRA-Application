package remocra.usecases.nature

import com.google.inject.Inject
import remocra.db.NatureRepository

class NatureUseCase {
    @Inject
    lateinit var natureRepository: NatureRepository
    fun getNatureForSelect(): List<NatureRepository.IdLibelleNature> = natureRepository.getNatureForSelect()
}
