package remocra.usecase.signalement

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.SignalementElementInput
import remocra.data.enums.ErrorType
import remocra.db.SignalementElementRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.SignalementElement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateSignalementElementUsecase
@Inject constructor(
    private val signalementElementRepository: SignalementElementRepository,
) :
    AbstractCUDUseCase<SignalementElementInput>(TypeOperation.INSERT) {

    override fun execute(userInfo: WrappedUserInfo, element: SignalementElementInput): SignalementElementInput {
        signalementElementRepository.insertSignalementElement(
            SignalementElement(
                signalementElementId = element.signalementElementId,
                signalementElementSignalementId = element.signalementElementSignalementId!!, // Vérification du non-null dans le checkContraintes
                signalementElementGeometrie = element.geometry,
                signalementElementDescription = element.description,
                signalementElementSousType = element.sousType,
            ),
        )
        signalementElementRepository.insertLiaisonAnomalie(element.signalementElementId, element.anomalies)

        return element
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.SIGNALEMENTS_C)) {
            throw RemocraResponseException(ErrorType.SIGNALEMENT_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: SignalementElementInput, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.signalementElementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.SIGNALEMENT_ELEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        ) }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: SignalementElementInput) {
        if (element.signalementElementSignalementId == null) {
            throw RemocraResponseException(ErrorType.SIGNALEMENT_ELEMENT_SIGNALEMENT_NULL)
        }
    }
}
