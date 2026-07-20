package remocra.usecase.atlas

import jakarta.inject.Inject
import remocra.data.AtlasImportResponse
import remocra.data.ImportZIPData
import remocra.data.ZipResource
import remocra.db.AtlasRepository
import remocra.db.TransactionManager
import remocra.usecase.AbstractUseCase
import remocra.usecase.atlas.zipstrategy.ZIPStrategyHandler
import remocra.usecase.atlas.zipstrategy.ZipArchive
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

class ImportAtlasZipUseCase @Inject constructor(
    private val atlasDocUseCase: AtlasDocUseCase,
    private val zipStrategyHandler: ZIPStrategyHandler,
    private val atlasRepository: AtlasRepository,
    private val transactionManager: TransactionManager,
) : AbstractUseCase() {

    fun importerAtlas(inputStream: InputStream): AtlasImportResponse =
        transactionManager.transactionResult {
            atlasRepository.deleteAll()

            val archive = read(inputStream)
            val importZIPData = ImportZIPData()

            try {
                zipStrategyHandler.validation(
                    archive,
                    importZIPData,
                )

                if (importZIPData.emptyErrors()) {
                    atlasDocUseCase.importZipEnregistrement(importZIPData, transactionManager)
                }

                AtlasImportResponse(importZIPData)
            } finally {
                archive.resources.forEach { resource ->
                    if (resource is ZipResource.File) {
                        Files.deleteIfExists(resource.tempFile)
                    }
                }
            }
        }

    private fun read(inputStream: InputStream): ZipArchive =
        ZipArchive(
            ZipInputStream(inputStream).use { zip ->
                generateSequence { zip.nextEntry }
                    .filterNot { it.isDirectory }
                    .map { entry ->
                        val tempFile = Files.createTempFile("atlas-import-", ".tmp")
                        Files.copy(zip, tempFile, StandardCopyOption.REPLACE_EXISTING)
                        ZipResource.File(path = entry.name, tempFile = tempFile)
                    }.toList()
            },
        )
}
