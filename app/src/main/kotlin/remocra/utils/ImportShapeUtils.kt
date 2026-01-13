package remocra.utils

import jakarta.inject.Inject
import remocra.usecase.document.DocumentUtils
import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.outputStream
import kotlin.io.path.relativeTo

class ImportShapeUtils {
    @Inject
    lateinit var documentUtils: DocumentUtils

    fun readZipFile(inputStream: InputStream, directory: Path): Path? {
        var fileShp: Path? = null
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(directory)

                // il y a un risque de "sortir" de 'directory' donc on "sanitize" un peu zipEntry.name
                // FIXME: le reste du code ne supporte que des fichiers, donc on pourrait:
                //  - skip les zipEntry.isDirectory
                //  - s'assurer qu'on n'a pas de niveau intermédiaire
                //  - utiliser alors Path(zipEntry.name).name
                val file = directory.resolve(Path(zipEntry.name).normalize().let { if (it.isAbsolute) it.relativeTo(Path("/")) else it })

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
