package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.DfciPanneauRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.DfciPanneau
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecase.AbstractCUDUseCase

/**
 * UseCase pour mettre à jour un panneau et rajouter une ligne dans la traçabilité
 */
class UpdateDfciPanneauUseCase @Inject constructor(private val dfciPanneauRepository: DfciPanneauRepository) : AbstractCUDUseCase<DfciPanneau>(
    TypeOperation.UPDATE,
) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Attente du tableau de droits
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DfciPanneau) {
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: DfciPanneau,
    ): DfciPanneau {
        dfciPanneauRepository.updateDfciPanneau(element)
        return element.copy()
    }

    override fun postEvent(element: DfciPanneau, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                element,
                element.dfciPanneauId,
                typeOperation,
                TypeObjet.DFCI_PANNEAU,
                userInfo.getInfosTracabilite(),
                dateUtils.now(),
            ),
        )
    }
}
