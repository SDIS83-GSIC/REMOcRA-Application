package remocra.usecase.crise.evenement.document

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.CriseDocumentData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.UpsertDocumentCriseUseCase

class CreateCriseDocument : AbstractCUDUseCase<CriseDocumentData>(TypeOperation.INSERT) {

    @Inject lateinit var upsertDocumentCriseUseCase: UpsertDocumentCriseUseCase

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: CriseDocumentData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listDocument = null),
                pojoId = element.criseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CRISE_DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: CriseDocumentData): CriseDocumentData {
        // insérer dans les documents
        if (element.listDocument != null) {
            upsertDocumentCriseUseCase.execute(
                userInfo,
                element.listDocument,
                transactionManager,
            )
        }

        return element.copy(listDocument = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CriseDocumentData) {
        // pas de contraintes
    }
}
