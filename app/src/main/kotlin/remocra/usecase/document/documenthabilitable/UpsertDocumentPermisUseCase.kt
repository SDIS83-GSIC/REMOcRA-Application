package remocra.usecase.document.documenthabilitable

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.AbstractDocumentData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.db.PermisRepository
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.document.AbstractUpsertDocumentUseCase
import java.nio.file.Path
import java.util.UUID

class UpsertDocumentPermisUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsPermis>() {

    @Inject lateinit var permisRepository: PermisRepository

    override fun insertLDocument(
        documentId: UUID,
        element: DocumentsData.DocumentsPermis,
        newDoc: AbstractDocumentData,
        mainTransactionManager: TransactionManager?,
    ) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            permisRepository.insertPermisDocument(documentId = documentId, permisId = element.objectId)
        }
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            permisRepository.deletePermisDocument(listeDocsToRemove)
        }
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?) {
        // Pas la possibilité d'Update, rien à spécifier ici.
    }

    override fun getRepertoire(): Path {
        return GlobalConstants.DOSSIER_DOCUMENT_PERMIS
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.hasDroit(droitWeb = Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_INSERT)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.hasDroit(droitWeb = Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_UPDATE)
        } else if (typeOperation == TypeOperation.DELETE && !userInfo.hasDroit(droitWeb = Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsPermis, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsData.DocumentsPermis(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_PERMIS,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DocumentsData.DocumentsPermis) {
        // Aucune contrainte.
    }
}
