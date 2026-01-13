package remocra.usecase.rcci

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.DocumentRepository
import remocra.db.RcciRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Rcci
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteRcciUseCase : AbstractCUDUseCase<Rcci>(TypeOperation.DELETE) {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var rcciRepository: RcciRepository

    @Inject
    lateinit var documentRepository: DocumentRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_DELETE_FORBIDDEN)
        }
    }

    override fun postEvent(element: Rcci, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rcciId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RCCI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: Rcci): Rcci {
        val documentIdList = rcciRepository.selectDocument(element.rcciId).map { it.documentId }
        rcciRepository.deleteDocument(element.rcciId)
        documentRepository.deleteDocumentByIds(documentIdList)
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_DOCUMENT_RCCI.resolve(element.rcciId.toString()))

        rcciRepository.deleteRcci(element.rcciId)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Rcci) {
        // no-op
    }
}
