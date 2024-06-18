package remocra.usecases.pei

import jakarta.inject.Inject
import remocra.db.PeiRepository
import remocra.web.pei.PeiEndPoint

class PeiUseCase {

    @Inject
    lateinit var peiRepository: PeiRepository

    fun getPeiWithFilter(param: PeiEndPoint.Params): List<PeiRepository.PeiForTableau> {
        return peiRepository.getPeiWithFilter(param)
    }
}
