package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.DfciPistesRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

/**
 * Usecase permettant de mettre a jour une piste
 */
class UpdatePisteUseCase @Inject constructor(private val pistesRepository: DfciPistesRepository) : AbstractCUDUseCase<DfciPiste>(
    TypeOperation.UPDATE,
) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DFCI_PISTE_U)) {
            throw RemocraResponseException(ErrorType.DFCI_PISTE_U_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DfciPiste) {
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: DfciPiste,
    ): DfciPiste {
        pistesRepository.updateDfciPiste(element)
        return element.copy()
    }

    override fun postEvent(element: DfciPiste, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.dfciPisteId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DFCI_PISTE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
