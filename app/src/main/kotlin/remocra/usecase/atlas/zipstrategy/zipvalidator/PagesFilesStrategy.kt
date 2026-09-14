package remocra.usecase.atlas.zipstrategy.zipvalidator
import jakarta.inject.Inject
import org.geotools.api.data.FileDataStoreFinder
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.data.AtlasDirectories
import remocra.data.ImportZIPData
import remocra.data.ImportZipError
import remocra.data.PageImportData
import remocra.usecase.atlas.zipstrategy.ZipArchive
import remocra.utils.ImportShapeUtils
import java.io.InputStream

class PagesFilesStrategy @Inject constructor(
    private val importShapeUtils: ImportShapeUtils,
) : ZIPStrategy {

    override fun validate(archive: ZipArchive, importZIPData: ImportZIPData) {
        val pages = archive.filesIn(AtlasDirectories.PAGES.name)
        val pdfs = pages.filter { it.path.endsWith(".pdf", true) }
        val shape = pages.firstOrNull { it.path.endsWith(".zip", true) }

        if (shape == null) {
            importZIPData += ImportZipError(
                "Aucun fichier SHAPE trouvé dans ${AtlasDirectories.PAGES.name}",
            )
            return
        }

        val shapes = shape.inputStream().use { input -> read(input) }

        pdfs.forEach { pdf ->
            val nomPdf = pdf.path.substringAfterLast("/").substringBeforeLast(".")
            val shapeData = shapes[nomPdf]

            if (shapeData == null) {
                importZIPData += ImportZipError("Aucune géométrie pour ${pdf.path}")
            } else {
                importZIPData += PageImportData(
                    nomFichierPdf = "$nomPdf.pdf",
                    actif = shapeData.actif,
                    geometrie = shapeData.geometry,
                    contenuPdf = requireNotNull(pdf.tempFile),
                )
            }
        }
    }

    private data class ShapeData(
        val geometry: Geometry,
        val actif: Boolean,
    )

    private enum class LineShape(val line: String) {
        GEOMETRY("the_geom"),
        FILE("fichier"),
        ACTIF("actif"),
    }

    private fun read(inputStream: InputStream): Map<String, ShapeData> {
        val shape = importShapeUtils.readZipFile(
            inputStream,
            GlobalConstants.DOSSIER_TMP_IMPORT_SITES,
        ) ?: return emptyMap()

        val store = FileDataStoreFinder.getDataStore(shape.toFile())
        val result = mutableMapOf<String, ShapeData>()

        store.featureSource.features.features().use { iterator ->
            while (iterator.hasNext()) {
                val feature = iterator.next()

                val geometry =
                    (
                        feature.properties.find {
                            it.name.localPart == LineShape.GEOMETRY.line
                        }?.value as? Geometry
                        )
                        ?.getGeometryN(0)
                        ?: continue

                val nomPdf =
                    feature.properties.find {
                        it.name.localPart == LineShape.FILE.line
                    }?.value as? String
                        ?: continue

                val actif =
                    feature.properties.find {
                        it.name.localPart == LineShape.ACTIF.line
                    }?.value as? Boolean
                        ?: true

                result[nomPdf] = ShapeData(
                    geometry = geometry,
                    actif = actif,
                )
            }
        }

        store.dispose()

        return result
    }
}
