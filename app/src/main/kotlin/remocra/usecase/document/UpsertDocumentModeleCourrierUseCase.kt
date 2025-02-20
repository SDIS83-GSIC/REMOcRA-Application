package remocra.usecase.document

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AbstractDocumentData
import remocra.data.AuteurTracabiliteData
import remocra.data.DocumentsData.DocumentModeleCourrierData
import remocra.data.DocumentsData.DocumentsModeleCourrier
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierDocument
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import java.util.UUID

class UpsertDocumentModeleCourrierUseCase : AbstractUpsertDocumentUseCase<DocumentsModeleCourrier>() {

    @Inject lateinit var modeleCourrierRepository: ModeleCourrierRepository

    override fun insertLDocument(documentId: UUID, element: DocumentsModeleCourrier, newDoc: AbstractDocumentData) {
        modeleCourrierRepository.insertLModeleCourrierDocument(
            LModeleCourrierDocument(
                element.objectId,
                documentId,
                (newDoc as DocumentModeleCourrierData).isMainReport,
            ),
        )
    }

    override fun deleteLDocument(listeDocsToRemove: Collection<UUID>) {
        modeleCourrierRepository.deleteLModeleCourrierDocument(listeDocsToRemove)
    }

    override fun updateLDocument(listToUpdate: Collection<AbstractDocumentData>) {
        val documentsNonRapportPrincipal = listToUpdate.filter {
            !(it as DocumentModeleCourrierData).isMainReport
        }.map { it.documentId!! }
        if (documentsNonRapportPrincipal.isNotEmpty()) {
            modeleCourrierRepository.updateIsMainReport(documentsNonRapportPrincipal, false)
        }

        val documentMainReport: UUID? = listToUpdate.firstOrNull {
            (it as DocumentModeleCourrierData).isMainReport
        }?.documentId
        if (documentMainReport != null) {
            modeleCourrierRepository.updateIsMainReport(listOf(documentMainReport), true)
        }
    }

    override fun getRepertoire(): String {
        return GlobalConstants.DOSSIER_MODELES_COURRIERS
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_FORBIDDEN)
        }
    }

    override fun postEvent(element: DocumentsModeleCourrier, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsModeleCourrier(
                    element.objectId,
                    element.listeDocsToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.objectId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_MODELE_COURRIER,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: DocumentsModeleCourrier) {
        // Un seul rapport principal
        if (element.listDocument.count { it.isMainReport } > 1) {
            throw RemocraResponseException(ErrorType.ADMIN_MODELE_COURRIER_DOCUMENT_MAIN_REPORT)
        }
    }
}
