package remocra.usecase.document

import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.AbstractDocumentData
import remocra.data.DocumentsData.DocumentData
import remocra.data.DocumentsData.DocumentsPei
import remocra.data.enums.ErrorType
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import java.util.UUID

class UpsertDocumentPeiUseCase : AbstractUpsertDocumentUseCase<DocumentsPei>() {

    override fun insertLDocument(documentId: UUID, element: DocumentsPei, newDoc: AbstractDocumentData, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            documentRepository.insertDocumentPei(element.objectId, documentId, (newDoc as DocumentData).isPhotoPei)
        }
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            documentRepository.deleteDocumentPei(listeDocsToRemove)
        }
    }
    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            val documentsNonPhoto = listToUpdate.filter { !(it as DocumentData).isPhotoPei }.map { it.documentId!! }
            if (documentsNonPhoto.isNotEmpty()) {
                documentRepository.updateIsPhotoPei(documentsNonPhoto, false)
            }

            val documentPhoto: UUID? = listToUpdate.firstOrNull { !(it as DocumentData).isPhotoPei }?.documentId
            if (documentPhoto != null) {
                documentRepository.updateIsPhotoPei(listOf(documentPhoto), true)
            }
        }
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_DOCUMENT_PEI
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.hasDroit(droitWeb = Droit.PEI_C)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_C)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.hasDroit(droitWeb = Droit.PEI_U)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: DocumentsPei, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsPei(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_PEI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DocumentsPei) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw RemocraResponseException(ErrorType.PEI_DOCUMENT_MEME_NOM)
        }

        // Une seule photo PEI
        if (element.listDocument.count { it.isPhotoPei } > 1) {
            throw RemocraResponseException(ErrorType.PEI_DOCUMENT_PHOTO)
        }
    }
}
