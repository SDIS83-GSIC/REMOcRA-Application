package remocra.usecase.toponymie

import jakarta.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.ToponymieRepository
import remocra.usecase.AbstractUseCase

class ToponymieUseCase
@Inject
constructor(
    private val toponymieRepository: ToponymieRepository,
) :
    AbstractUseCase() {
    fun getToponymieForSelect(): Collection<IdCodeLibelleData> = toponymieRepository.getToponymieForSelect()
}
