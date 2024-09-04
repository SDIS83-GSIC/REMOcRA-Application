package remocra.usecases.document

import com.google.inject.Inject
import jakarta.servlet.http.Part
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.TypeSourceModification
import remocra.db.DocumentRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime
import java.util.UUID

class UpsertDocumentPeiUseCase : AbstractCUDUseCase<UpsertDocumentPeiUseCase.DocumentsPei>(TypeOperation.UPDATE) {
    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var documentRepository: DocumentRepository

    data class DocumentsPei(
        val peiId: UUID,
        val documentIdToRemove: List<UUID>,
        val listDocument: List<DocumentData>,
        val listDocumentParts: List<Part>,
    )

    data class DocumentData(
        val documentId: UUID?,
        val documentNomFichier: String,
        val isPhotoPei: Boolean,
    )

    override fun checkDroits(userInfo: UserInfo) {
        // TODO vérifier les droits
    }

    override fun postEvent(element: DocumentsPei, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo =
                // On ne sauvegarde pas les bytearray
                DocumentsPei(
                    element.peiId,
                    element.documentIdToRemove,
                    element.listDocument,
                    listOf(),
                ),
                pojoId = element.peiId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.DOCUMENT_PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: DocumentsPei): DocumentsPei {
        // On supprime les documents de la liste documentIdToRemove
        val listeDocumentToSupprime = documentRepository.getDocumentByIds(element.documentIdToRemove)

        // Sur le serveur
        listeDocumentToSupprime.forEach {
            documentUtils.deleteFile(it.documentNomFichier, it.documentRepertoire)
        }

        // Puis en base
        documentRepository.deleteDocumentPei(element.documentIdToRemove)
        documentRepository.deleteDocumentByIds(element.documentIdToRemove)

        // On ajoute ceux qui sont à ajouter (l'id est null)
        val nouveauDocuments = element.listDocument.filter { it.documentId == null }

        nouveauDocuments.forEach { newDoc ->
            val filePart = element.listDocumentParts.find { it.name == "document_${newDoc.documentNomFichier}" }
            val repertoire = GlobalConstants.DOSSIER_DOCUMENT_PEI + "/${element.peiId}"
            documentUtils.saveFile(filePart!!.inputStream.readAllBytes(), newDoc.documentNomFichier, repertoire)

            val idDocument = UUID.randomUUID()
            // On sauvegarde en base
            documentRepository.insertDocument(
                Document(
                    documentId = idDocument,
                    documentDate = ZonedDateTime.now(clock),
                    documentRepertoire = repertoire,
                    documentNomFichier = newDoc.documentNomFichier,
                ),
            )

            documentRepository.insertDocumentPei(element.peiId, idDocument, newDoc.isPhotoPei)
        }

        // On met à jour le isPhotoPei pour les autres
        val listToUpdate = element.listDocument.minus(nouveauDocuments)
        val documentsNonPhoto = listToUpdate.filter { !it.isPhotoPei }.map { it.documentId!! }
        if (documentsNonPhoto.isNotEmpty()) {
            documentRepository.updateIsPhotoPei(documentsNonPhoto, false)
        }

        val documentPhoto: UUID? = element.listDocument.minus(nouveauDocuments).firstOrNull { !it.isPhotoPei }?.documentId
        if (documentPhoto != null) {
            documentRepository.updateIsPhotoPei(listOf(documentPhoto), true)
        }
        return element
    }

    override fun checkContraintes(element: DocumentsPei) {
        // Si même nom => lève une exeption
        if (element.listDocument.groupingBy { it.documentNomFichier }.eachCount().any { it.value > 1 }) {
            throw IllegalArgumentException("Les documents d'un même PEI ne doivent pas avoir le même nom.")
        }

        // Une seule photo PEI
        if (element.listDocument.count { it.isPhotoPei } > 1) {
            throw IllegalArgumentException("Un seul document peut représenter la photo du PEI")
        }
    }
}
