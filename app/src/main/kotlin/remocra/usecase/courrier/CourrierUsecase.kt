package remocra.usecase.courrier

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.Params
import remocra.db.CourrierRepository
import remocra.db.ModuleRepository
import java.util.UUID

class CourrierUsecase {

    @Inject
    lateinit var courrierRepository: CourrierRepository

    @Inject
    lateinit var moduleRepository: ModuleRepository

    fun getCourrierCompletWithThematique(
        moduleId: UUID,
        userInfo: WrappedUserInfo,
        params: Params<CourrierRepository.Filter, CourrierRepository.Sort>,
    ): Collection<CourrierRepository.CourrierComplet> {
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        return courrierRepository.getCourrierCompletWithThematique(
            listeThematiqueId = listeThematiqueId,
            userInfo = userInfo,
            params = params,
        )
    }

    fun countCourrierCompletWithThematique(
        moduleId: UUID,
        userInfo: WrappedUserInfo,
        params: Params<CourrierRepository.Filter, CourrierRepository.Sort>,
    ): Int {
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        return courrierRepository.countCourrierCompletWithThematique(
            listeThematiqueId = listeThematiqueId,
            userInfo = userInfo,
            params = params,
        )
    }
}
