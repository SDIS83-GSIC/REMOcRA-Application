package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiAvecTournees
import remocra.data.enums.ErrorType
import remocra.db.TourneeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.tournee.UpdateLTourneePeiUseCase.LTourneePeiToInsert

class DesaffecterTourneesUseCase @Inject constructor(private var tourneeRepository: TourneeRepository) : AbstractCUDUseCase<List<PeiAvecTournees>>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.TOURNEE_DESAFFECTER_U)) {
            throw RemocraResponseException(ErrorType.TOURNEE_DESAFFECTER)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: List<PeiAvecTournees>,
    ) {
        val reservedTournees = tourneeRepository.getTourneeReservedFromList(element.flatMap { it.tournees }.map { it.idTournee })
        if (reservedTournees.isNotEmpty()) {
            throw RemocraResponseException(
                ErrorType.PEI_TOURNEE_RESERVE,
                reservedTournees.joinToString(", ") { it.tourneeLibelle },
            )
        }
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: List<PeiAvecTournees>,
    ): List<PeiAvecTournees> {
        element.forEach { pei ->
            pei.tournees.forEach { tourneeRepository.deleteLTourneePeiByTourneeAndPeiId(it.idTournee, pei.peiId) }
        }
        return element
    }

    override fun postEvent(element: List<PeiAvecTournees>, userInfo: WrappedUserInfo) {
        val lTourneePei = tourneeRepository.fetchLTourneePeiByTourneeIds(element.flatMap { pei -> pei.tournees }.map { it.idTournee })
        lTourneePei.groupBy { it.tourneeId }.forEach { (tourneeId, listLTourneePei) ->
            eventBus.post(
                TracabiliteEvent(
                    pojo = LTourneePeiToInsert(
                        tourneeId = tourneeId,
                        listLTourneePei = listLTourneePei,
                    ),
                    pojoId = tourneeId,
                    typeOperation = typeOperation,
                    typeObjet = TypeObjet.TOURNEE_PEI,
                    auteurTracabilite = userInfo.getInfosTracabilite(),
                    date = dateUtils.now(),
                ),
            )
        }
    }
}
