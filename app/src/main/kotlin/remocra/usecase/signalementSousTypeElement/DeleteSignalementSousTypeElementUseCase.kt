package remocra.usecase.signalementSousTypeElement

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.SignalementRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.SignalementSousTypeElement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteSignalementSousTypeElementUseCase @Inject constructor(private val signalementRepository: SignalementRepository) : AbstractCUDUseCase<SignalementSousTypeElement>(TypeOperation.DELETE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: SignalementSousTypeElement, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.signalementSousTypeElementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.SIGNALEMENT_SOUS_TYPE_ELEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: SignalementSousTypeElement): SignalementSousTypeElement {
        signalementRepository.deleteById(element.signalementSousTypeElementId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: SignalementSousTypeElement) {
        // no-op
    }
}
