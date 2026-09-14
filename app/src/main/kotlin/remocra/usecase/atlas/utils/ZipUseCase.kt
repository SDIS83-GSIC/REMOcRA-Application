package remocra.usecase.atlas.utils

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.usecase.AbstractUseCase
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipUseCase @Inject constructor(
    private val createAtlasPDFUseCase: CreateAtlasPDFUseCase,
) : AbstractUseCase() {

    fun createZipWithPdf(
        documentsPages: List<Document>,
        annexesList: List<AtlasAnnexe>,
        documentsAnnexes: List<Document>,
    ): StreamingOutput {
        return StreamingOutput { output ->

            ZipOutputStream(output).use { zip ->
                zip.putNextEntry(ZipEntry("document.pdf"))

                createAtlasPDFUseCase.createCombinedPdf(
                    documentsPages,
                    annexesList,
                    documentsAnnexes,
                    zip,
                )

                zip.closeEntry()
            }
        }
    }
}
