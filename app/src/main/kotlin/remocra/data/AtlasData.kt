package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.tables.pojos.AtlasAnnexe
import remocra.db.jooq.remocra.tables.pojos.AtlasDocument
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream

data class AtlasElements(
    val atlasAnnexe: List<AtlasAnnexe>,
    val atlasDocument: List<AtlasDocument>,
)

// //////////// ZIP //////////////
data class ImportZipError(
    val message: String,
)

enum class AtlasDirectories {
    PAGES, ANNEXES
}

sealed interface ImportDocument {
    val nomFichierPdf: String
    val contenuPdf: Path
    val actif: Boolean
}

data class AnnexeCsv(
    val nomFichierPdf: String,
    val actif: Boolean,
    val nomAnnexeFileName: String?,
)

data class PageImportData(
    override val nomFichierPdf: String,
    override val contenuPdf: Path,
    override val actif: Boolean,
    val geometrie: Geometry,
) : ImportDocument

data class AnnexeImportData(
    override val nomFichierPdf: String,
    override val contenuPdf: Path,
    override val actif: Boolean,
    val nomAnnexeFileName: String,
) : ImportDocument

sealed interface ZipResource {
    val path: String

    data class File(override val path: String, val tempFile: Path) : ZipResource {
        fun inputStream(): InputStream = tempFile.inputStream()
    }
}

data class ImportZIPData(
    private val pages: MutableList<PageImportData> = mutableListOf(),
    private val annexes: MutableList<AnnexeImportData> = mutableListOf(),
    private val errors: MutableList<ImportZipError> = mutableListOf(),
) {
    fun getAnnexes() = annexes.toList()
    fun emptyErrors() = errors.isEmpty()
    fun getPages() = pages.toList()
    fun getErrors() = errors.toList()

    operator fun plusAssign(annexe: AnnexeImportData) {
        annexes += annexe
    }

    operator fun plusAssign(page: PageImportData) {
        pages += page
    }

    operator fun plusAssign(error: ImportZipError) {
        errors += error
    }
}

data class AtlasImportResponse(
    val importZipData: ImportZIPData,
)
