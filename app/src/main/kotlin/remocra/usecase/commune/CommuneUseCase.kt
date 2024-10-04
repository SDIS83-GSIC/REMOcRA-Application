package remocra.usecase.commune

import com.google.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository
import remocra.usecase.AbstractUseCase

class CommuneUseCase : AbstractUseCase() {
    @Inject
    lateinit var communeRepository: CommuneRepository
    fun getCommuneForSelect(): List<IdCodeLibelleData> = communeRepository.getCommuneForSelect()
}
