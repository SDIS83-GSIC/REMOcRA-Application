package remocra.utils

import jakarta.inject.Inject
import remocra.usecase.document.DocumentUtils
import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.outputStream

class ImportShapeUtils {
    @Inject
    lateinit var documentUtils: DocumentUtils

    fun readZipFile(inputStream: InputStream, directory: Path): Path? {
        var fileShp: Path? = null
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(directory)

                // On ignore les dossiers et les fichiers dans des sous-dossiers
                if (zipEntry.isDirectory) {
                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                    continue
                }
                // On extrait uniquement le nom du fichier (pas de sous-dossier)
                val fileName = Path(zipEntry.name).name
                val file = directory.resolve(fileName)

                file.outputStream().use { output ->
                    zipInputStream.copyTo(output)
                }

                if (file.extension == "shp") {
                    fileShp = file
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
        return fileShp
    }
}
