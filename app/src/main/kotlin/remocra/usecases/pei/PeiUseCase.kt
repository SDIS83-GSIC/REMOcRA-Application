package remocra.usecases.pei

import jakarta.inject.Inject
import remocra.db.PeiRepository
import remocra.web.pei.PeiEndPoint

/**
 * UseCase regroupant tous les services devant remonter de l'information sur les PEI. <br />
 *
 * /!\ Aucun service ne doit modifier l'état des PEI, sinon passer par les UseCases dédiés :
 * * [CreatePeiUseCase]
 * * [UpdatePeiUseCase]
 * * [DeletePeiUseCase]
 */
class PeiUseCase {

    @Inject
    lateinit var peiRepository: PeiRepository

    fun getPeiWithFilter(param: PeiEndPoint.Params): List<PeiRepository.PeiForTableau> {
        return peiRepository.getPeiWithFilter(param)
    }
}
