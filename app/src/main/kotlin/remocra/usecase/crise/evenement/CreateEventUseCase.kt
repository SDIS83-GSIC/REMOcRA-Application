package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementData
import remocra.data.MessageData
import remocra.data.enums.ErrorType
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.document.UpsertDocumentEvenementUseCase
import java.util.UUID

class CreateEventUseCase : AbstractCUDGeometrieUseCase<EvenementData>(TypeOperation.INSERT) {

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject lateinit var messageRepository: MessageRepository

    @Inject lateinit var upsertDocumentEvenementUseCase: UpsertDocumentEvenementUseCase

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }
    override fun getListGeometrie(element: EvenementData): Collection<Geometry> {
        return element.evenementGeometrie?.let { listOf(it) } ?: emptyList()
    }

    override fun ensureSrid(element: EvenementData): EvenementData {
        if (element.evenementGeometrie != null && element.evenementGeometrie.srid != appSettings.srid) {
            return element.copy(
                evenementGeometrie = transform(element.evenementGeometrie),
            )
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: EvenementData) {
    }

    override fun execute(userInfo: WrappedUserInfo, element: EvenementData): EvenementData {
        // - evenement
        evenementRepository.add(element)

        // - document
        if (element.listeDocuments != null) {
            upsertDocumentEvenementUseCase.execute(
                userInfo,
                element.listeDocuments,
                transactionManager,
            )
        }
        // - message
        messageRepository.add(
            MessageData(
                messageObjet = "Création d'évènement",
                messageDescription = "",
                messageDateConstat = dateUtils.now(),
                messageImportance = element.evenementImportance,
                messageOrigine = element.evenementOrigine,
                messageTags = element.evenementTags.joinToString(),
                messageId = UUID.randomUUID(),
                messageEvenementId = element.evenementId,
                messageUtilisateurId = element.evenementUtilisateurId,
            ),
        )

        return element.copy(listeDocuments = null)
    }

    override fun postEvent(element: EvenementData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeDocuments = null),
                pojoId = element.evenementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
