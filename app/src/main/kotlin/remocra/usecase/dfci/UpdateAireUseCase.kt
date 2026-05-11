package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.DfciAiresRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecase.AbstractCUDUseCase

/**
 * UseCase pour mettre à jour l'aire donné selon son ID, rajoute une ligne dans la traçabilité
 */
class UpdateAireUseCase @Inject constructor(private val dfciAiresRepository: DfciAiresRepository) : AbstractCUDUseCase<DfciAire>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Attendre le tableau de droit
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DfciAire) {
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: DfciAire,
    ): DfciAire {
        dfciAiresRepository.updateDfciAire(element)
        return element.copy()
    }

    override fun postEvent(element: DfciAire, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.dfciAireId,
                typeOperation,
                typeObjet = TypeObjet.DFCI_AIRE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
