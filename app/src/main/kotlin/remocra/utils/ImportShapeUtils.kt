package remocra.utils

import jakarta.inject.Inject
import remocra.usecase.document.DocumentUtils
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportShapeUtils {
    @Inject
    lateinit var documentUtils: DocumentUtils

    fun readZipFile(inputStream: InputStream, directory: String): File? {
        var fileShp: File? = null
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(directory)

                val file = File(directory + zipEntry.name)

                zipInputStream.readBytes().inputStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (zipEntry.name.contains(".shp")) {
                    fileShp = file
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
        return fileShp
    }
}
