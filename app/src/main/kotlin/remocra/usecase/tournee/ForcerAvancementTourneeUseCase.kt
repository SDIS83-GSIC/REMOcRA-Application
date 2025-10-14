package remocra.usecase.tournee

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.TourneeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class ForcerAvancementTourneeUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
) : AbstractCUDUseCase<Tournee>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.TOURNEE_FORCER_POURCENTAGE_E)) {
            throw RemocraResponseException(ErrorType.TOURNEE_FORCER_AVANCEMENT_FORBIDDEN)
        }
    }

    override fun postEvent(element: Tournee, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.tourneeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TOURNEE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Tournee) {
        if (element.tourneeReservationUtilisateurId != null) {
            throw RemocraResponseException(ErrorType.TOURNEE_LECTURE_SEULE)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: Tournee): Tournee {
        tourneeRepository.setAvancementTournee(element.tourneeId, element.tourneePourcentageAvancement!!)
        return element
    }
}
