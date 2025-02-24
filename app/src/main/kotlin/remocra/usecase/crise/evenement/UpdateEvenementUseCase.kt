package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.EvenementData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.EvenementRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.UpsertDocumentEvenementUseCase

class UpdateEvenementUseCase : AbstractCUDUseCase<EvenementData>(TypeOperation.UPDATE) {

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject private lateinit var upsertDocumentEvenementUseCase: UpsertDocumentEvenementUseCase

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.EVENEMENT_U)) {
            throw RemocraResponseException(ErrorType.EVENEMENT_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: EvenementData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeDocument = null),
                pojoId = element.evenementId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: EvenementData): EvenementData {
        // - evenement
        val newElement = if (element.evenementEstFerme == true) {
            element.copy(evenementStatut = EvenementStatut.CLOS, evenementDateCloture = dateUtils.now())
        } else {
            element.copy()
        }

        evenementRepository.updateEvenement(newElement)
        // - document
        if (element.listeDocument != null) {
            upsertDocumentEvenementUseCase.execute(userInfo, element.listeDocument, transactionManager)
        }

        return element.copy(listeDocument = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: EvenementData) {
        // rien ici
    }
}
