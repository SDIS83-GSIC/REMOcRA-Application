package remocra.usecase.atlas.utils

import org.apache.pdfbox.io.IOUtils
import org.apache.pdfbox.multipdf.PDFMergerUtility
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.usecase.AbstractUseCase
import java.io.File
import java.io.OutputStream

class CreateAtlasPDFUseCase : AbstractUseCase() {

    fun createCombinedPdf(
        documentsPages: List<Document>,
        annexesList: List<AtlasAnnexe>,
        documentsAnnexes: List<Document>,
        output: OutputStream,
    ) {
        val merger = PDFMergerUtility()
        merger.destinationStream = output

        val documentsById = documentsAnnexes.associateBy { it.documentId }

        annexesList
            .mapNotNull { documentsById[it.atlasAnnexeDocumentId] }
            .forEach {
                merger.addSource(getFile(it))
            }

        documentsPages.forEach {
            merger.addSource(getFile(it))
        }

        merger.mergeDocuments(IOUtils.createTempFileOnlyStreamCache())
    }

    private fun getFile(document: Document): File =
        File(document.documentRepertoire, document.documentNomFichier)
}
