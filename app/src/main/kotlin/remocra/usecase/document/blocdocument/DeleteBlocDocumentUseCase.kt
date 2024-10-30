package remocra.usecase.document.blocdocument

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.BlocDocumentData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.BlocDocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteBlocDocumentUseCase : AbstractCUDUseCase<BlocDocumentData>(TypeOperation.DELETE) {

    @Inject lateinit var blocDocumentRepository: BlocDocumentRepository

    @Inject lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DOCUMENTS_A)) {
            throw RemocraResponseException(ErrorType.BLOC_DOCUMENT_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: BlocDocumentData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    document = null,
                ),
                pojoId = element.blocDocumentId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.BLOC_DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: BlocDocumentData): BlocDocumentData {
        // On récupére le document lié à notre bloc document
        val document = blocDocumentRepository.getDocumentByBlocDocument(element.blocDocumentId)
            ?: throw RemocraResponseException(ErrorType.BLOC_DOCUMENT_DOCUMENT_NOT_FOUND)

        // On supprime le document sur le disque
        documentUtils.deleteFile(document.documentNomFichier, document.documentRepertoire)
        documentUtils.deleteDirectory(document.documentRepertoire)

        // Puis on supprime les données en base
        blocDocumentRepository.deleteThematiqueBlocDocument(element.blocDocumentId)
        blocDocumentRepository.deleteProfilDroitBlocDocument(element.blocDocumentId)

        blocDocumentRepository.deleteBlocDocument(element.blocDocumentId)

        return element.copy(document = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: BlocDocumentData) {
    }
}
