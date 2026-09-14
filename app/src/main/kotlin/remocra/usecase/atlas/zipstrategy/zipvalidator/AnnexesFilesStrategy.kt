package remocra.usecase.atlas.zipstrategy.zipvalidator

import jakarta.inject.Inject
import remocra.csv.CsvReader
import remocra.data.AnnexeCsv
import remocra.data.AnnexeImportData
import remocra.data.AtlasDirectories
import remocra.data.ImportZIPData
import remocra.data.ImportZipError
import remocra.usecase.atlas.zipstrategy.ZipArchive

class AnnexesFilesStrategy @Inject constructor(
    private val csvReader: CsvReader,
) : ZIPStrategy {

    override fun validate(archive: ZipArchive, importZIPData: ImportZIPData) {
        val annexes = archive.filesIn(AtlasDirectories.ANNEXES.name)
        val pdfs = annexes.filter { it.path.endsWith(".pdf", true) }
        val csv = annexes.firstOrNull { it.path.endsWith(".csv", true) }

        if (csv == null) {
            importZIPData += ImportZipError("CSV${AtlasDirectories.ANNEXES.name} absent")
            return
        }

        val lignes = csv.inputStream().use { input ->
            csvReader.readCsvFile<AnnexeCsv>(
                input,
                ',',
            )
        } ?: mutableListOf()

        val actifs = lignes.associateBy { it.nomFichierPdf }

        pdfs.forEach { pdf ->
            val nomFichierPdf = pdf.path.substringAfterLast("/")
            val ligne = actifs[nomFichierPdf]

            if (ligne == null) {
                importZIPData += ImportZipError("PDF $nomFichierPdf absent du CSV")
            } else {
                importZIPData += AnnexeImportData(
                    nomFichierPdf = nomFichierPdf,
                    contenuPdf = requireNotNull(pdf.tempFile),
                    actif = ligne.actif,
                    nomAnnexeFileName = ligne.nomAnnexeFileName ?: nomFichierPdf,
                )
            }
        }
    }
}
