package remocra.usecase.document

import jakarta.inject.Inject
import remocra.data.DocumentsData.DocumentData
import remocra.db.DocumentRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class DocumentPeiUseCase : AbstractUseCase() {

    @Inject lateinit var documentRepository: DocumentRepository

    fun execute(peiId: UUID): List<DocumentData> {
        val listeDocument = documentRepository.getDocumentByPei(peiId)

        return listeDocument.map {
            DocumentData(
                it.documentId,
                it.documentNomFichier,
                it.isPhotoPei,
            )
        }
    }

    /**
     * Retourne le path du document passé en paramètre
     */
    fun telecharger(documentId: UUID) =
        documentRepository.getById(documentId)
}
