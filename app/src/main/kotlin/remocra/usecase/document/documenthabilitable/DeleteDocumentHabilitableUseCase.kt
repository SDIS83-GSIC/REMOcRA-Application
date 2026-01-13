package remocra.usecase.document.documenthabilitable

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DocumentHabilitableData
import remocra.data.enums.ErrorType
import remocra.db.DocumentHabilitableRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.nio.file.Paths

class DeleteDocumentHabilitableUseCase : AbstractCUDUseCase<DocumentHabilitableData>(TypeOperation.DELETE) {

    @Inject lateinit var documentHabilitableRepository: DocumentHabilitableRepository

    @Inject lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DOCUMENTS_A)) {
            throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: DocumentHabilitableData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    document = null,
                ),
                pojoId = element.documentHabilitableId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DOCUMENT_HABILITABLE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: DocumentHabilitableData): DocumentHabilitableData {
        // On récupére le document lié à notre document habilitable
        val document = documentHabilitableRepository.getDocumentByDocumentHabilitable(element.documentHabilitableId)
            ?: throw RemocraResponseException(ErrorType.DOCUMENT_HABILITABLE_DOCUMENT_NOT_FOUND)

        // On supprime le document sur le disque
        val path = Paths.get(document.documentRepertoire)
        documentUtils.deleteFile(document.documentNomFichier, path)
        documentUtils.deleteDirectory(path)

        // Puis on supprime les données en base
        documentHabilitableRepository.deleteThematiqueDocumentHabilitable(element.documentHabilitableId)
        documentHabilitableRepository.deleteGroupeFonctionnalitesDocumentHabilitable(element.documentHabilitableId)

        documentHabilitableRepository.deleteDocumentHabilitable(element.documentHabilitableId)

        return element.copy(document = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DocumentHabilitableData) {
    }
}
