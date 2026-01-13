package remocra.usecase.debitsimultane

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DebitSimultaneData
import remocra.data.enums.ErrorType
import remocra.db.DebitSimultaneRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import kotlin.io.path.Path

class DeleteDebitSimultaneUseCase : AbstractCUDUseCase<DebitSimultaneData>(TypeOperation.DELETE) {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.DEBITS_SIMULTANES_A)) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_FORBIDDEN)
        }
    }

    override fun postEvent(element: DebitSimultaneData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.debitSimultaneId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DEBIT_SIMULTANE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: DebitSimultaneData): DebitSimultaneData {
        val mapDocumentByDebitMesure = debitSimultaneRepository.getDocumentByDebitSimultaneMesureId(element.debitSimultaneId)

        element.listeDebitSimultaneMesure.forEach {
            // On supprime le lien avec les PEI
            debitSimultaneRepository.deleteLDebitSimultaneMesurePei(it.debitSimultaneMesureId!!)

            // Puis les documents sur le disque
            val document = mapDocumentByDebitMesure[it.debitSimultaneMesureId]
            if (document != null) {
                val path = Path(document.documentRepertoire)
                documentUtils.deleteFile(document.documentNomFichier, path)
                documentUtils.deleteDirectory(path)
            }
        }

        debitSimultaneRepository.deleteDebitSimultaneMesureByDebitSimultaneId(element.debitSimultaneId)

        // Delete les débits simultanés
        debitSimultaneRepository.deleteDebitSimultane(element.debitSimultaneId)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: DebitSimultaneData) {
        // noop
    }
}
