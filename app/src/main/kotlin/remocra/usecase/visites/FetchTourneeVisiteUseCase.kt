package remocra.usecase.visites

import jakarta.inject.Inject
import remocra.db.TourneeRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class FetchTourneeVisiteUseCase
@Inject
constructor(
    private val tourneeRepository: TourneeRepository,
) :
    AbstractUseCase() {

    fun fetchTourneeVisite(tourneeId: UUID): TourneeInformation =
        TourneeInformation(
            tourneeLibelle = tourneeRepository.getTourneeLibelle(tourneeId),
            listPeiInformations = tourneeRepository.getPeiVisiteTourneeInformation(tourneeId),
            listCDPByPeiTournee = tourneeRepository.getListLastPeiCDPByTournee(tourneeId),
        )

    data class TourneeInformation(
        val tourneeLibelle: String,
        val listPeiInformations: List<TourneeRepository.PeiVisiteTourneeInformation>,
        val listCDPByPeiTournee: List<TourneeRepository.CDPByPeiId>,
    )
}
