package remocra.usecase.document.documenthabilitable

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentHabilitableData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentHabilitableRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteDocumentHabilitableUseCase : AbstractCUDUseCase<DocumentHabilitableData>(TypeOperation.DELETE) {

    @Inject lateinit var documentHabilitableRepository: DocumentHabilitableRepository

    @Inject lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DOCUMENTS_A)) {
            throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_FORBIDDEN_DELETE)
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
        // On récupére le document lié à notre document habilitable
        val document = documentHabilitableRepository.getDocumentByDocumentHabilitable(element.documentHabilitableId)
            ?: throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_DOCUMENT_NOT_FOUND)

        // On supprime le document sur le disque
        documentUtils.deleteFile(document.documentNomFichier, document.documentRepertoire)
        documentUtils.deleteDirectory(document.documentRepertoire)

        // Puis on supprime les données en base
        documentHabilitableRepository.deleteThematiqueDocumentHabilitable(element.documentHabilitableId)
        documentHabilitableRepository.deleteProfilDroitDocumentHabilitable(element.documentHabilitableId)

        documentHabilitableRepository.deleteDocumentHabilitable(element.documentHabilitableId)

        return element.copy(document = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentHabilitableData) {
    }
}
