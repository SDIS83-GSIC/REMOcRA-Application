package remocra.usecase.debitsimultane

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.DebitSimultaneData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.DebitSimultaneRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class DeleteDebitSimultaneUseCase : AbstractCUDUseCase<DebitSimultaneData>(TypeOperation.DELETE) {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var documentUtils: DocumentUtils

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.DEBITS_SIMULTANES_A)) {
            throw RemocraResponseException(ErrorType.DEBIT_SIMULTANE_FORBIDDEN)
        }
    }

    override fun postEvent(element: DebitSimultaneData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.debitSimultaneId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.DEBIT_SIMULTANE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: DebitSimultaneData): DebitSimultaneData {
        val mapDocumentByDebitMesure = debitSimultaneRepository.getDocumentByDebitSimultaneMesureId(element.debitSimultaneId)

        element.listeDebitSimultaneMesure.forEach {
            // On supprime le lien avec les PEI
            debitSimultaneRepository.deleteLDebitSimultaneMesurePei(it.debitSimultaneMesureId!!)

            // Puis les documents sur le disque
            val document = mapDocumentByDebitMesure[it.debitSimultaneMesureId]
            if (document != null) {
                documentUtils.deleteFile(document.documentNomFichier, document.documentRepertoire)
                documentUtils.deleteDirectory(document.documentRepertoire)
            }
        }

        debitSimultaneRepository.deleteDebitSimultaneMesureByDebitSimultaneId(element.debitSimultaneId)

        // Delete les débits simultanés
        debitSimultaneRepository.deleteDebitSimultane(element.debitSimultaneId)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DebitSimultaneData) {
        // noop
    }
}
