package remocra.usecase.atlas.zipstrategy.zipvalidator

import remocra.data.ImportZIPData
import remocra.usecase.atlas.zipstrategy.ZipArchive

interface ZIPStrategy {
    fun validate(archive: ZipArchive, importZIPData: ImportZIPData)
}
