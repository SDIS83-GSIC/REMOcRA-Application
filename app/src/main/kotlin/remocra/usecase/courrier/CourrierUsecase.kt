package remocra.usecase.courrier

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
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
        userInfo: UserInfo?,
        params: Params<CourrierRepository.Filter, CourrierRepository.Sort>,
    ): Collection<CourrierRepository.CourrierComplet> {
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }
        if (userInfo == null) {
            throw ForbiddenException()
        }

        return courrierRepository.getCourrierCompletWithThematique(
            listeThematiqueId = listeThematiqueId,
            userInfo = userInfo,
            params = params,
        )
    }

    fun countCourrierCompletWithThematique(
        moduleId: UUID,
        userInfo: UserInfo?,
        params: Params<CourrierRepository.Filter, CourrierRepository.Sort>,
    ): Int {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        return courrierRepository.countCourrierCompletWithThematique(
            listeThematiqueId = listeThematiqueId,
            userInfo = userInfo,
            params = params,
        )
    }
}
