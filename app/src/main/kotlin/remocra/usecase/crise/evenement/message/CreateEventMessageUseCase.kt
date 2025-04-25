package remocra.usecase.crise.evenement.message

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.MessageData
import remocra.data.enums.ErrorType
import remocra.db.MessageRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateEventMessageUseCase : AbstractCUDUseCase<MessageData>(TypeOperation.INSERT) {

    @Inject
    lateinit var messageRepository: MessageRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: MessageData) {
    }

    override fun execute(userInfo: WrappedUserInfo, element: MessageData): MessageData {
        messageRepository.add(element)
        return element
    }

    override fun postEvent(element: MessageData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.messageId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MESSAGE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
