package remocra.usecase.document

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.AbstractDocumentData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.db.CriseRepository
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import java.util.UUID

// Utilisation de "DocumentsEvenement" pour les crises, car les documents des crises ont les mêmes caractèristiques que ceux des évènements
class UpsertDocumentCriseUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsEvenement>() {

    @Inject
    lateinit var criseRepository: CriseRepository

    override fun insertLDocument(documentId: UUID, element: DocumentsData.DocumentsEvenement, newDoc: AbstractDocumentData, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            criseRepository.insertCriseDocument(documentId, element.objectId)
        }
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?) {
        // pas utile pour l'instant
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?) {
        // Rien ici
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_DOCUMENT_CRISE
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.hasDroit(droitWeb = Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.hasDroit(droitWeb = Droit.CRISE_U)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsEvenement, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                DocumentsData.DocumentsEvenement(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.CRISE_DOCUMENT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DocumentsData.DocumentsEvenement) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw RemocraResponseException(ErrorType.CRISE_DOCUMENT_MEME_NOM)
        }
    }
}
