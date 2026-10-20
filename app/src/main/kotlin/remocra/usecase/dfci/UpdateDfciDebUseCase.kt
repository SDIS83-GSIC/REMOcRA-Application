package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.DfciDebRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

/**
 * UseCase permettant de mettre à jour les débroussaillements et d'ajouter l'opération dans la traçabilité
 */
class UpdateDfciDebUseCase @Inject constructor(private val dfciDebRepository: DfciDebRepository) : AbstractCUDUseCase<DfciDeb>(
    TypeOperation.UPDATE,
) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DFCI_DEB_U)) {
            throw RemocraResponseException(ErrorType.DFCI_DEB_U_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DfciDeb) {
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: DfciDeb,
    ): DfciDeb {
        dfciDebRepository.updateDeb(element)
        return element.copy()
    }

    override fun postEvent(element: DfciDeb, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                element,
                element.dfciDebId,
                typeOperation,
                TypeObjet.DFCI_DEB,
                userInfo.getInfosTracabilite(),
                dateUtils.now(),
            ),
        )
    }
}
