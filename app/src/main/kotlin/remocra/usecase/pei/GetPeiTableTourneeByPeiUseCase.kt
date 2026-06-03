package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiAvecTournees
import remocra.data.TourneeInfo
import remocra.db.OrganismeRepository
import remocra.db.PeiRepository
import remocra.db.TourneeRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetPeiTableTourneeByPeiUseCase @Inject constructor(
    private var peiRepository: PeiRepository,
    private var tourneRepository: TourneeRepository,
    private var organismeRepository: OrganismeRepository,
) : AbstractUseCase() {

    fun execute(peiIds: Set<UUID>, userInfo: WrappedUserInfo): List<PeiAvecTournees> {
        return peiIds.map { peiId ->
            val peiData = peiRepository.getPeiTourneeInfo(peiId)
            val tourneesList = tourneRepository.getTourneesByPeiId(peiId, userInfo)
            val tournees = tourneesList.map { tournee ->
                TourneeInfo(
                    idTournee = tournee.tourneeId,
                    libelleTournee = tournee.tourneeLibelle,
                    organismeTournee = organismeRepository.getLibelleById(tournee.tourneeOrganismeId),
                )
            }

            PeiAvecTournees(
                peiId = peiData.peiId,
                peiNumeroComplet = peiData.peiNumeroComplet,
                tournees = tournees,
            )
        }
    }
}
