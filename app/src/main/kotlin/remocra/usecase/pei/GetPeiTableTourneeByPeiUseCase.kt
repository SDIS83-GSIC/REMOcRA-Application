package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiAvecTournees
import remocra.data.TourneeInfo
import remocra.db.TourneeRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetPeiTableTourneeByPeiUseCase @Inject constructor(
    private var tourneRepository: TourneeRepository,
) : AbstractUseCase() {

    fun execute(peiIds: Set<UUID>, userInfo: WrappedUserInfo): List<PeiAvecTournees> =
        tourneRepository.getPeiAvecTournees(peiIds, userInfo.affiliatedOrganismeIds, userInfo.isSuperAdmin)
            .groupBy { it.peiId }
            .map { (_, rows) ->
                val pei = rows.first()
                PeiAvecTournees(
                    peiId = pei.peiId,
                    peiNumeroComplet = pei.peiNumeroComplet,
                    tournees = rows.map { TourneeInfo(it.tourneeId, it.tourneeLibelle, it.organismeLibelle) },
                )
            }
}
