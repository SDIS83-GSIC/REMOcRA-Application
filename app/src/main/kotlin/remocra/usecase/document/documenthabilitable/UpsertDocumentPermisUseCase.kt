package remocra.usecase.document.documenthabilitable

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AbstractDocumentData
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.PermisRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.document.AbstractUpsertDocumentUseCase
import java.util.UUID

class UpsertDocumentPermisUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsPermis>() {

    @Inject lateinit var permisRepository: PermisRepository

    override fun insertLDocument(
        documentId: UUID,
        element: DocumentsData.DocumentsPermis,
        newDoc: AbstractDocumentData,
    ) {
        permisRepository.insertPermisDocument(documentId = documentId, permisId = element.objectId)
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>) {
        permisRepository.deletePermisDocument(listeDocsToRemove)
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>) {
        // Pas la possibilité d'Update, rien à spécifier ici.
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_DOCUMENT_PERMIS
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.droits.contains(Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_INSERT)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.droits.contains(Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_UPDATE)
        } else if (typeOperation == TypeOperation.DELETE && !userInfo.droits.contains(Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsPermis, userInfo: UserInfo) {
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
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentsData.DocumentsPermis) {
        // Aucune contrainte.
    }
}
