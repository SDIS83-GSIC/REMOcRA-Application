package remocra.usecase.toponymie

import jakarta.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.ToponymieRepository
import remocra.usecase.AbstractUseCase

class ToponymieUseCase : AbstractUseCase() {
    @Inject
    lateinit var toponymieRepository: ToponymieRepository
    fun getToponymieForSelect(): Collection<IdCodeLibelleData> = toponymieRepository.getToponymieForSelect()
}
