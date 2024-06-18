package remocra.usecases.commune

import com.google.inject.Inject
import remocra.data.GlobalData.IdLibelleData
import remocra.db.CommuneRepository

class CommuneUseCase {
    @Inject
    lateinit var communeRepository: CommuneRepository
    fun getCommuneForSelect(): List<IdLibelleData> = communeRepository.getCommuneForSelect()
}
