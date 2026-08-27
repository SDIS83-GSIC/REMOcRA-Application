package remocra.usecase.atlas

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import remocra.data.AtlasDirectories
import remocra.data.AtlasElementsTemplate
import remocra.usecase.AbstractUseCase
import remocra.usecase.atlas.zipstrategy.ZipBuilder
import java.io.File

class DownloadTemplateZipAtlasUseCase @Inject constructor(
    private val atlasTypesSeparatedUseCase: AtlasTypesSeparatedUseCase,
) : AbstractUseCase() {

    fun execute(): StreamingOutput {
        val builder = ZipBuilder()

        AtlasDirectories.entries.forEach {
            builder.addDirectory(it.name)
        }

        createAtlasFile(atlasTypesSeparatedUseCase.execute())?.let {
            builder.addFile(it, "${AtlasDirectories.PAGES.name}/atlas_names.txt")
        }

        builder.addFile(createAnnexCsvTemplate(), "${AtlasDirectories.ANNEXES.name}/annexes.csv")
        builder.addFile(createReadme(), "README.txt")

        return builder.build()
    }

    private fun createAtlasFile(atlasList: AtlasElementsTemplate): File? {
        if (atlasList.atlasDocument.isEmpty() || atlasList.atlasAnnexe.isEmpty()) return null

        return createTempFile(
            "atlas_names",
            ".txt",
            buildString {
                appendLine("Noms des fichiers PDF enregistrés dans l'application :")
                atlasList.atlasDocument.forEach {
                    appendLine(it)
                }

                appendLine("Noms des annexes PDF :")
                atlasList.atlasAnnexe.forEach {
                    appendLine(it)
                }
                appendLine("Attention : ce fichier ne doit pas apparaître dans le document ZIP final.")
            },
        )
    }

    private fun createAnnexCsvTemplate() =
        createTempFile(
            "annexes_template",
            ".csv",
            """
                nom_fichier_pdf,actif,nom_annexe
                exemple_annexe.pdf,TRUE, exemple
            """.trimIndent(),
        )

    private fun createReadme() =
        createTempFile(
            "README",
            ".txt",
            """
                Contenu du ZIP

                ├── PAGES/
                │   ├── 001.pdf
                │   ├── 002.pdf
                │   ├── …
                │   └── shape.zip
                │
                ├── ANNEXES/ (facultatif)
                │   ├── annexe1.pdf
                │   ├── …
                │   └── annexes.csv
                └──

                Le fichier SHAPE doit contenir :
                - fichier
                - geometrie
                - actif

                Le fichier CSV doit contenir :
                - nom_fichier_pdf
                - actif
                - nom_annexe (les valeurs peuvent être null)
                
                Attention : ce fichier ne doit pas apparaître dans le document ZIP final.
            """.trimIndent(),
        )

    private fun createTempFile(prefix: String, suffix: String, content: String): File =
        File.createTempFile(prefix, suffix).apply {
            writeText(content)
        }
}
