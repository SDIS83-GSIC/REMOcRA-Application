package remocra.usecases.commune

import com.google.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository

class CommuneUseCase {
    @Inject
    lateinit var communeRepository: CommuneRepository
    fun getCommuneForSelect(): List<IdCodeLibelleData> = communeRepository.getCommuneForSelect()
}
