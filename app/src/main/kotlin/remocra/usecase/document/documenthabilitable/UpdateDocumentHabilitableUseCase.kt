package remocra.usecase.document.documenthabilitable

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentHabilitableData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentHabilitableRepository
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.DocumentHabilitable
import remocra.db.jooq.remocra.tables.pojos.LProfilDroitDocumentHabilitable
import remocra.db.jooq.remocra.tables.pojos.LThematiqueDocumentHabilitable
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class UpdateDocumentHabilitableUseCase : AbstractCUDUseCase<DocumentHabilitableData>(TypeOperation.UPDATE) {

    @Inject lateinit var documentRepository: DocumentRepository

    @Inject lateinit var documentHabilitableRepository: DocumentHabilitableRepository

    @Inject lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DOCUMENTS_A)) {
            throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_FORBIDDEN_UPDATE)
        }
    }

    override fun postEvent(element: DocumentHabilitableData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    document = null,
                ),
                pojoId = element.documentHabilitableId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DOCUMENT_HABILITABLE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: DocumentHabilitableData): DocumentHabilitableData {
        val document = documentHabilitableRepository.getDocumentByDocumentHabilitable(element.documentHabilitableId)
            ?: throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_DOCUMENT_NOT_FOUND)

        // Si l'utilisateur a changé de document
        if (element.document != null) {
            // On supprime le fichier sur le disque et on remet le bon
            documentUtils.deleteFile(document.documentNomFichier, document.documentRepertoire)
            val repertoire = GlobalConstants.DOSSIER_DOCUMENT_HABILITABLE + "/${element.documentHabilitableId}"
            documentUtils.saveFile(
                element.document.inputStream.readAllBytes(),
                element.document.submittedFileName,
                repertoire,
            )
            // On met à jour le nom du fichier
            documentRepository.updateDocument(element.document.submittedFileName, repertoire, document.documentId)
        }

        documentHabilitableRepository.updateDocumentHabilitable(
            DocumentHabilitable(
                documentHabilitableId = element.documentHabilitableId,
                documentId = document.documentId,
                documentHabilitableLibelle = element.documentHabilitableLibelle,
                documentHabilitableDescription = element.documentHabilitableDescription,
                documentHabilitableDateMaj = dateUtils.now(),
            ),
        )

        documentHabilitableRepository.deleteThematiqueDocumentHabilitable(element.documentHabilitableId)
        element.listeThematiqueId?.forEach {
            documentHabilitableRepository.insertThematiqueDocumentHabilitable(
                LThematiqueDocumentHabilitable(
                    documentHabilitableId = element.documentHabilitableId,
                    thematiqueId = it,
                ),
            )
        }

        documentHabilitableRepository.deleteProfilDroitDocumentHabilitable(element.documentHabilitableId)
        element.listeProfilDroitId?.forEach {
            documentHabilitableRepository.insertProfilDroitDocumentHabilitable(
                LProfilDroitDocumentHabilitable(
                    documentHabilitableId = element.documentHabilitableId,
                    profilDroitId = it,
                ),
            )
        }

        return element.copy(document = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentHabilitableData) {
    }
}
