package remocra.usecase.crise

import jakarta.inject.Inject
import remocra.db.CriseRepository
import remocra.db.CriseRepository.TypeCriseComplete
import remocra.usecase.AbstractUseCase

class CriseUseCase : AbstractUseCase() {
    @Inject
    lateinit var criseRepository: CriseRepository
    fun getTypeCriseForSelect(): Collection<TypeCriseComplete> = criseRepository.getCriseForSelect()
}
