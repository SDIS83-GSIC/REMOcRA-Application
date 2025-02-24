package remocra.usecase.crise.evenement.message

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.MessageData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: MessageData) {
        if (messageRepository.checkNumeroExists(element.messageId)) {
            throw RemocraResponseException(ErrorType.CRISE_NUMERO_UNIQUE)
        }
    }

    override fun execute(userInfo: UserInfo?, element: MessageData): MessageData {
        messageRepository.add(element)
        return element
    }

    override fun postEvent(element: MessageData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.messageId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.MESSAGE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }
}
