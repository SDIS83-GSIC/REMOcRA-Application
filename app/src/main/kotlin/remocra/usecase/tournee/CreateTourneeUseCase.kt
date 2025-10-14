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

class CreateTourneeUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
) : AbstractCUDUseCase<Tournee>(TypeOperation.INSERT) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.TOURNEE_A)) {
            throw RemocraResponseException(ErrorType.TOURNEE_GESTION_FORBIDDEN)
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
        // La paire organisme_id et tournee_libelle doit etre unique
        if (tourneeRepository.tourneeAlreadyExists(element.tourneeId, element.tourneeLibelle, tourneeOrganismeId = element.tourneeOrganismeId)) {
            throw RemocraResponseException(ErrorType.TOURNEE_ALREADY_EXISTS)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: Tournee): Tournee {
        tourneeRepository.insertTournee(element)
        return element
    }
}
