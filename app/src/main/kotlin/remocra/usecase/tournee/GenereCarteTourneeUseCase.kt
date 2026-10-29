package remocra.usecase.tournee

import fr.opensagres.xdocreport.converter.ConverterTypeTo
import fr.opensagres.xdocreport.converter.ConverterTypeVia
import fr.opensagres.xdocreport.converter.Options
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import jakarta.inject.Inject
import jakarta.ws.rs.core.MultivaluedHashMap
import okhttp3.OkHttpClient
import okhttp3.Request
import org.odftoolkit.odfdom.doc.OdfTextDocument
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.TourneeRepository
import remocra.exception.RemocraResponseException
import remocra.geoserver.GeoserverModule
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.DateUtils.Companion.PATTERN_MINUTE_SECONDE
import remocra.utils.DateUtils.Companion.PATTERN_NATUREL_DATE_ONLY
import remocra.utils.addQueryParameters
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import javax.imageio.ImageIO
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants

class GenereCarteTourneeUseCase @Inject constructor(
    private val parametresProvider: ParametresProvider,
    private val tourneeRepository: TourneeRepository,
    private val geoserverSettings: GeoserverModule.GeoserverSettings,
    private val httpClient: OkHttpClient,
    private val appSettings: AppSettings,
    private val documentUtils: DocumentUtils,
) : AbstractUseCase() {

    fun getCarteTournee(userInfo: WrappedUserInfo, tourneeId: UUID): CarteTournee {
        val bufferCarteParam = parametresProvider.getParametreInt(ParametreEnum.BUFFER_CARTE.toString()) ?: 100

        // Récupération de la géométrie brute de la tournée
        val tourneeGeometrie = tourneeRepository.getGeometrieTournee(
            tourneeId,
            userInfo.isSuperAdmin,
            userInfo.zoneCompetence?.zoneIntegrationId,
        )

        require(tourneeGeometrie.isNotEmpty()) { "Aucun PEI associé à la tournée : $tourneeId" }

        val coordinates = tourneeGeometrie.map { it.peiGeometrie.coordinate }

        // Calcul de la bbox initiale
        val minX = coordinates.minOf { it.x }
        val minY = coordinates.minOf { it.y }
        val maxX = coordinates.maxOf { it.x }
        val maxY = coordinates.maxOf { it.y }

        // Détermination de l'orientation basée sur les données
        val xSize = maxX - minX
        val ySize = maxY - minY
        val orientation = if (xSize >= ySize) Orientation.PAYSAGE else Orientation.PORTRAIT

        // Charger le template avec la bonne orientation
        val templatePath = GlobalConstants.DOSSIER_CARTE_TOURNEE_TEMPLATE
            .resolve("carte-tournee-${orientation.name.lowercase()}.odt")
            .toFile()
        val (width, height) = getTemplateImageDimensions(templatePath.absolutePath)
        val templateRatio = width.toDouble() / height.toDouble()

        // Génération de la bbox avec buffer en utilisant le ratio du template
        val bufferedMapData = addBuffer(
            coordinates = Coordonnee(minX = minX, minY = minY, maxX = maxX, maxY = maxY),
            buffer = bufferCarteParam,
            templateRatio = templateRatio,
        )

        val infoCarteTournee = GenererCarteData(
            bbox = bufferedMapData.bbox,
            nomTournee = tourneeRepository.getTourneeLibelle(tourneeId),
            nbPei = tourneeRepository.getNbPei(tourneeId),
            tourneeId = tourneeId,
            orientation = bufferedMapData.orientation,
        )

        // Construction des paramètre WMS GetMap
        val map: Map<String, String> = mapOf(
            "LAYERS" to GlobalConstants.COUCHE_TOURNEE,
            "TRANSPARENT" to "true",
            "SERVICE" to "wms",
            "REQUEST" to "GetMap",
            "STYLES" to "",
            "FORMAT" to "image/png",
            "SRS" to "EPSG:${appSettings.srid}",
            "BBOX" to infoCarteTournee.bbox.toBBOX(),
            "VIEWPARAMS" to "tournee_id:${infoCarteTournee.tourneeId}",
            "WIDTH" to width.toString(),
            "HEIGHT" to height.toString(),
        )

        // Construction de l'URL + appel HTTP vers GeoServer
        val queryParameters = MultivaluedHashMap(map)
        val url = geoserverSettings.url
            .newBuilder()
            .addPathSegment("remocra")
            .addPathSegment("wms")
            .addQueryParameters(queryParameters)
            .build()

        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        // Renvoi l'image PNG généré par GeoServer
        val image = httpClient.newCall(request).execute().body()?.bytes()

        if (image == null || image.isEmpty()) {
            throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
        }

        // Création du provider image
        val imageProvider = ByteArrayImageProvider(image)

        // Extraction de la largeur et hauteur réelle de l'image PNG
        val imagePNGWidth = getImagePNGDimensions(image)

        // Calcul de l'échelle de la carte basée sur la taille réelle de l'image
        val largeurReelle = bufferedMapData.bbox[2] - bufferedMapData.bbox[0] // maxX - minX
        val scale = "1:" + (largeurReelle / (imagePNGWidth.toFloat() / SCALE_FACTOR)).toInt()

        // On crée le fichier PDF
        val report = XDocReportRegistry.getRegistry().loadReport(
            FileInputStream(templatePath),
            TemplateEngineKind.Freemarker,
        )
        val context = report.createContext()

        // On ajoute l'image dans le contexte
        context.put("carte", imageProvider)
        report.createFieldsMetadata().addFieldAsImage("carte", true)

        val libelleTournee = tourneeRepository.getTourneeLibelle(tourneeId)

        // On ajoute aussi des variables textuelles
        context.put("libelleTournee", libelleTournee)
        context.put("nbPei", tourneeRepository.getNbPei(tourneeId).toString())
        context.put("dateGeneration", dateUtils.format(dateUtils.now(), PATTERN_NATUREL_DATE_ONLY))
        context.put("scale", scale)

        val nomFichier = "$libelleTournee-${
            dateUtils.format(dateUtils.now(), PATTERN_MINUTE_SECONDE)
        }"

        // On s'assure que le répertoire existe, sinon on le crée
        documentUtils.ensureDirectory(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)

        val pdfFile = GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE.resolve("$nomFichier.pdf").toFile()

        val options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM)

        FileOutputStream(pdfFile).use {
            report.convert(context, options, it)
        }

        // Retourner directement le contenu du PDF généré
        return CarteTournee(
            fileName = nomFichier,
            file = pdfFile.readBytes(),
        )
    }
}

data class CarteTournee(
    val fileName: String,
    val file: ByteArray,
)

data class GenererCarteData(
    val bbox: List<Double>,
    val nomTournee: String,
    val nbPei: Int,
    val tourneeId: UUID,
    val orientation: Orientation,
)

fun List<Double>.toBBOX(): String {
    require(size == 4) { "La bbox doit contenir 4 coordonné" }
    return this.joinToString(separator = ",") { it.toString() }
}

private fun addBuffer(
    coordinates: Coordonnee,
    buffer: Int,
    templateRatio: Double = 1.4142, // Ratio par défaut du template (format A4)
): GeoserveurData {
    // Application du buffer brut - garder les dimensions buffées
    val xMinBuffered = coordinates.minX - buffer
    val xMaxBuffered = coordinates.maxX + buffer
    val yMinBuffered = coordinates.minY - buffer
    val yMaxBuffered = coordinates.maxY + buffer

    var xSize = xMaxBuffered - xMinBuffered
    var ySize = yMaxBuffered - yMinBuffered

    // Garder les tailles buffées comme minimum
    val xSizeMin = xSize
    val ySizeMin = ySize

    val xCentroid = (xMinBuffered + xMaxBuffered) / 2
    val yCentroid = (yMinBuffered + yMaxBuffered) / 2

    // Déterminer orientation et forcer le ratio du template
    val isPaysage = xSize > ySize

    if (isPaysage) {
        // Paysage : ajuster ySize pour respecter le ratio, mais jamais en-dessous du buffer
        ySize = maxOf(xSize / templateRatio, ySizeMin)
    } else {
        // Portrait : ajuster xSize pour respecter le ratio, mais jamais en-dessous du buffer
        xSize = maxOf(ySize * templateRatio, xSizeMin)
    }

    // Recentrage de la bbox après ajustement
    val xMin = xCentroid - xSize / 2
    val xMax = xCentroid + xSize / 2
    val yMin = yCentroid - ySize / 2
    val yMax = yCentroid + ySize / 2

    // Orientation réelle après transformation
    val orientation = if (xSize >= ySize) Orientation.PAYSAGE else Orientation.PORTRAIT

    return GeoserveurData(bbox = listOf(xMin, yMin, xMax, yMax), orientation = orientation)
}

data class GeoserveurData(
    val bbox: List<Double>,
    val orientation: Orientation,
)

data class Coordonnee(
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
)

enum class Orientation {
    PAYSAGE,
    PORTRAIT,
}

const val SCALE_FACTOR = 10 // facteur d'échelle pour convertir les pixels en unités de carte

/**
 * Extrait les dimensions de l'image du template ODT via ODFDOM.
 *
 * @param templatePath Chemin absolu du fichier ODT
 * @return Pair(width, height) en pixels
 * @throws RemocraResponseException si l'image n'est pas trouvée
 */
private fun getTemplateImageDimensions(templatePath: String): Pair<Int, Int> {
    return try {
        val doc = OdfTextDocument.loadDocument(templatePath)
        val xpath: XPath = doc.getContentDom().getXPath()

        val requete = "//draw:frame[@draw:name='carte']"
        val frame = xpath.evaluate(requete, doc.getContentDom(), XPathConstants.NODE) as DrawFrameElement?
            ?: throw RemocraResponseException(ErrorType.IMAGE_INVALIDE)

        val widthStr = frame.svgWidthAttribute
        val heightStr = frame.svgHeightAttribute
        val width = parseUnitToPx(widthStr).toInt()
        val height = parseUnitToPx(heightStr).toInt()

        width to height
    } catch (_: RemocraResponseException) {
        throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
    } catch (e: Exception) {
        throw e
    }
}

/**
 * Parse une valeur avec unité (ex: "10cm", "100px") en pixels.
 *
 * @param value La valeur avec unité
 * @return La valeur en pixels
 */
private fun parseUnitToPx(value: String): Double {
    val raw = value.trim()
    if (raw.isEmpty())throw RemocraResponseException(ErrorType.IMAGE_CONVERSION_PIXEL_IMPOSSSIBLE)

    val numberPart = raw.takeWhile { it.isDigit() || it == '.' }
    if (numberPart.isEmpty())throw RemocraResponseException(ErrorType.IMAGE_CONVERSION_PIXEL_IMPOSSSIBLE)

    val number = numberPart.toDoubleOrNull() ?: throw RemocraResponseException(ErrorType.IMAGE_CONVERSION_PIXEL_IMPOSSSIBLE)
    val unitPart = raw.drop(numberPart.length).trim()
    val unit = UnitToPx.from(unitPart) ?: throw RemocraResponseException(ErrorType.IMAGE_CONVERSION_PIXEL_IMPOSSSIBLE)

    return UnitValue(number, unit).toPx()
}

private enum class UnitToPx(val facteur: Double) {
    PX(1.0),
    CM(37.8),
    MM(3.78),
    IN(96.0),
    PT(1.333),
    ;

    fun toPx(value: Double): Double = value * facteur

    companion object {
        fun from(raw: String): UnitToPx? =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
    }
}

private data class UnitValue(val value: Double, val unit: UnitToPx) {
    fun toPx(): Double = unit.toPx(value)
}

/**
 * Extrait la largeur ou hauteur réelle d'une image PNG à partir de ses bytes bruts.
 *
 * @param imageBytes Les bytes bruts du fichier PNG
 * @param isHeight Si true, retourne la hauteur, sinon la largeur
 * @return La dimension de l'image en pixels
 * @throws RemocraResponseException si l'image est invalide ou illisible
 */
private fun getImagePNGDimensions(imageBytes: ByteArray?, isHeight: Boolean = false): Int {
    if (imageBytes == null || imageBytes.isEmpty()) {
        throw RemocraResponseException(ErrorType.IMAGE_INVALIDE)
    }

    return try {
        val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
            ?: throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
        if (isHeight) bufferedImage.height else bufferedImage.width
    } catch (e: RemocraResponseException) {
        throw e
    } catch (_: Exception) {
        throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
    }
}
