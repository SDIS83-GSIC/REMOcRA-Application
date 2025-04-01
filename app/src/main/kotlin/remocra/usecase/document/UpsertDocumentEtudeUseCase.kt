package remocra.usecase.document

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AbstractDocumentData
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import java.util.UUID

class UpsertDocumentEtudeUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsEtude>() {

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun insertLDocument(documentId: UUID, element: DocumentsData.DocumentsEtude, newDoc: AbstractDocumentData, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            couvertureHydrauliqueRepository.insertEtudeDocument(documentId, element.objectId, (newDoc as DocumentsData.DocumentEtudeData).etudeDocumentLibelle)
        }
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            couvertureHydrauliqueRepository.deleteEtudeDocument(listeDocsToRemove)
        }
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>, mainTransactionManager: TransactionManager?) {
        (mainTransactionManager ?: transactionManager).transactionResult(mainTransactionManager == null) {
            listToUpdate.forEach {
                couvertureHydrauliqueRepository.updateEtudeDocument(it.documentId!!, (it as DocumentsData.DocumentEtudeData).etudeDocumentLibelle)
            }
        }
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_DOCUMENT_ETUDE
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.droits.contains(Droit.ETUDE_C)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_C)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.droits.contains(Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsEtude, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsData.DocumentsEtude(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_ETUDE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentsData.DocumentsEtude) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw RemocraResponseException(ErrorType.ETUDE_DOCUMENT_MEME_NOM)
        }
    }
}
