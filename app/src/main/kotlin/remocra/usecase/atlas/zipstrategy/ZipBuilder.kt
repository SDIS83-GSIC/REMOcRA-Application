package remocra.usecase.atlas.zipstrategy

import jakarta.ws.rs.core.StreamingOutput
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipBuilder {
    private val directories = mutableListOf<String>()
    private val files = mutableListOf<ZipFile>()

    fun addDirectory(name: String) {
        directories += name
    }

    fun addFile(file: File, path: String) {
        files += ZipFile(file, path)
    }

    fun build(): StreamingOutput =
        StreamingOutput { output ->
            ZipOutputStream(output).use { zip ->

                directories.forEach {
                    zip.putNextEntry(ZipEntry("$it/"))
                    zip.closeEntry()
                }

                files.forEach { entry ->
                    zip.putNextEntry(ZipEntry(entry.path))

                    Files.copy(entry.file.toPath(), zip)

                    zip.closeEntry()
                }
            }
        }

    private data class ZipFile(
        val file: File,
        val path: String,
    )
}
