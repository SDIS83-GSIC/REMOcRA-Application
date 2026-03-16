package remocra.usecase.indisponibilitetemporaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class LeverIndispoTempPeiUseCase
@Inject constructor(
    private val cloreIndisponibiliteTemporaireUseCase: CloreIndisponibiliteTemporaireUseCase,
    private val indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository,
) : AbstractUseCase() {

    fun execute(
        userInfo: WrappedUserInfo,
        element: List<UUID>,
    ): Result {
        element.forEach {
            val result = cloreIndisponibiliteTemporaireUseCase.execute(
                userInfo = userInfo,
                element = indisponibiliteTemporaireRepository.getWithListPeiById(it),
            )
            if (result !is Result.Success) {
                return result
            }
        }
        return Result.Success()
    }
}
