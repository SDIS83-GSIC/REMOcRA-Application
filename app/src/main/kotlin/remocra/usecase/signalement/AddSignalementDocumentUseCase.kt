package remocra.usecase.signalement

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.AbstractDocumentData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.db.SignalementRepository
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.document.AbstractUpsertDocumentUseCase
import java.nio.file.Path
import java.util.UUID

class AddSignalementDocumentUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsEvenement>() {

    @Inject
    lateinit var signalementRepository: SignalementRepository

    override fun insertLDocument(documentId: UUID, element: DocumentsData.DocumentsEvenement, newDoc: AbstractDocumentData, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            signalementRepository.insertSignalementDocument(documentId, element.objectId)
        }
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?) {
        // no-op
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?) {
        // no-op
    }

    override fun getRepertoire(): Path {
        return GlobalConstants.DOSSIER_DOCUMENT_SIGNALEMENT
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.hasDroit(droitWeb = Droit.SIGNALEMENTS_C)) {
            throw RemocraResponseException(ErrorType.SIGNALEMENT_FORBIDDEN_INSERT)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.hasDroit(droitWeb = Droit.SIGNALEMENTS_C)) { throw RemocraResponseException(ErrorType.SIGNALEMENT_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsEvenement, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsData.DocumentsEvenement(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_SIGNALEMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DocumentsData.DocumentsEvenement) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw RemocraResponseException(ErrorType.EVENEMENT_DOCUMENT_MEME_NOM)
        }
    }
}
