package remocra.usecase.document

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AbstractDocumentData
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentsData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CriseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import java.util.UUID

// Utilisation de "DocumentsEvenement" pour les crises, car les documents des crises ont les mêmes caractèristiques que ceux des évènements
class UpsertDocumentCriseUseCase : AbstractUpsertDocumentUseCase<DocumentsData.DocumentsEvenement>() {

    @Inject
    lateinit var criseRepository: CriseRepository

    override fun insertLDocument(documentId: UUID, element: DocumentsData.DocumentsEvenement, newDoc: AbstractDocumentData) {
        criseRepository.insertCriseDocument(documentId, element.objectId)
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>) {
        // pas utile pour l'instant
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>) {
        // Rien ici
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_DOCUMENT_CRISE
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (typeOperation == TypeOperation.INSERT && !userInfo.droits.contains(Droit.CRISE_C)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_C)
        } else if (typeOperation == TypeOperation.UPDATE && !userInfo.droits.contains(Droit.CRISE_U)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: DocumentsData.DocumentsEvenement, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                DocumentsData.DocumentsEvenement(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.CRISE_DOCUMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentsData.DocumentsEvenement) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw RemocraResponseException(ErrorType.CRISE_DOCUMENT_MEME_NOM)
        }
    }
}
