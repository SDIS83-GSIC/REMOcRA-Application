package remocra.usecases.document

import com.google.inject.Inject
import remocra.db.DocumentRepository
import java.util.UUID

class DocumentPeiUseCase {

    @Inject lateinit var documentRepository: DocumentRepository

    fun execute(peiId: UUID): List<UpsertDocumentPeiUseCase.DocumentData> {
        val listeDocument = documentRepository.getDocumentByPei(peiId)

        return listeDocument.map {
            UpsertDocumentPeiUseCase.DocumentData(
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
