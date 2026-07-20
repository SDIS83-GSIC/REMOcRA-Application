package remocra.usecase.atlas.zipstrategy

import jakarta.inject.Inject
import remocra.data.ImportZIPData
import remocra.usecase.atlas.zipstrategy.zipvalidator.AnnexesFilesStrategy
import remocra.usecase.atlas.zipstrategy.zipvalidator.PagesFilesStrategy

class ZIPStrategyHandler @Inject constructor(
    annexesFilesStrategy: AnnexesFilesStrategy,
    pagesFilesStrategy: PagesFilesStrategy,
) {
    private val strategies = listOf(
        annexesFilesStrategy,
        pagesFilesStrategy,
    )

    fun validation(archive: ZipArchive, importZIPData: ImportZIPData) {
        strategies.forEach { it.validate(archive, importZIPData) }
    }
}
