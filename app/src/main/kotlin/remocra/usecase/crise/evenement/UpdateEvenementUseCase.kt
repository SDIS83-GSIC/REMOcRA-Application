package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.EvenementData
import remocra.data.MessageData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EvenementStatut
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.document.UpsertDocumentEvenementUseCase
import java.util.UUID

class UpdateEvenementUseCase : AbstractCUDGeometrieUseCase<EvenementData>(TypeOperation.UPDATE) {

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject lateinit var messageRepository: MessageRepository

    @Inject private lateinit var upsertDocumentEvenementUseCase: UpsertDocumentEvenementUseCase
    override fun getListGeometrie(element: EvenementData): Collection<Geometry> {
        return element.evenementGeometrie?.let { listOf(it) } ?: emptyList()
    }

    override fun ensureSrid(element: EvenementData): EvenementData {
        if (element.evenementGeometrie != null && element.evenementGeometrie.srid != appSettings.srid) {
            return element.copy(
                evenementGeometrie = transform(element.evenementGeometrie),
            )
        }
        return element
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.EVENEMENT_U)) {
            throw RemocraResponseException(ErrorType.EVENEMENT_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: EvenementData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(listeDocuments = null),
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
        if (element.listeDocuments != null) {
            upsertDocumentEvenementUseCase.execute(userInfo, element.listeDocuments, transactionManager)
        }
        // - message
        messageRepository.add(
            MessageData(
                messageObjet = "Redéfinition d'évènement",
                messageDescription = "",
                messageDateConstat = dateUtils.now(),
                messageImportance = element.evenementImportance,
                messageOrigine = element.evenementOrigine,
                messageTags = element.evenementTags.joinToString(),
                messageId = UUID.randomUUID(),
                messageEvenementId = element.evenementId,
                messageUtilisateurId = element.evenementUtilisateurId,
            ),
        )

        return element.copy(listeDocuments = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: EvenementData) {
        // rien ici
    }
}
