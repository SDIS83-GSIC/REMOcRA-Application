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
import remocra.utils.DateUtils
import remocra.utils.addQueryParameters
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import javax.imageio.ImageIO

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

        // Génération de la bbox avec buffer et ratio A4
        val buffer = addBuffer(coordinates = Coordonnee(minX = minX, minY = minY, maxX = maxX, maxY = maxY), buffer = bufferCarteParam)

        val infoCarteTournee = GenererCarteData(
            bbox = buffer.bbox,
            nomTournee = tourneeRepository.getTourneeLibelle(tourneeId),
            nbPei = tourneeRepository.getNbPei(tourneeId),
            tourneeId = tourneeId,
            orientation = buffer.orientation,
        )

        // Calcul des dimensions image WMS selon orientation (A4 ratio)
        val (width, height) = getWMSDimensions(infoCarteTournee.orientation)

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

        // Détection dynamique de la taille de l'image
        val imageProvider = ByteArrayImageProvider(image)

        // Extraction de la largeur réelle de l'image PNG
        val imagePNGWidth = getImagePNGDimensions(image)

        // Calcul de l'échelle de la carte basée sur la taille réelle de l'image
        val largeurReelle = buffer.bbox[2] - buffer.bbox[0] // maxX - minX
        val scale = "1:" + (largeurReelle / (imagePNGWidth.toFloat() / SCALE_FACTOR)).toInt()

        // On crée le fichier PDF
        val report = XDocReportRegistry.getRegistry().loadReport(
            FileInputStream(GlobalConstants.DOSSIER_CARTE_TOURNEE_TEMPLATE.resolve("carte-tournee-${buffer.orientation.name.lowercase()}.odt").toFile()),
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
        context.put("dateGeneration", dateUtils.format(dateUtils.now(), DateUtils.Companion.PATTERN_NATUREL_DATE_ONLY))
        context.put("scale", scale)

        val nomFichier = "$libelleTournee-${
            dateUtils.format(dateUtils.now(), "yyyy-MM-dd-HH-mm-ss")
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

fun addBuffer(
    coordinates: Coordonnee,
    buffer: Int,
    tailleMinX: Double = TAILLE_MIN_X_A4_PORTRAIT,
    tailleMinY: Double = TAILLE_MIN_Y_A4_PORTRAIT,
): GeoserveurData {
    // Application du buffer brut
    val XminRaw = coordinates.minX - buffer
    val XmaxRaw = coordinates.maxX + buffer
    val YminRaw = coordinates.minY - buffer
    val YmaxRaw = coordinates.maxY + buffer

    var X = XmaxRaw - XminRaw
    var Y = YmaxRaw - YminRaw

    val Xcentroid = (XminRaw + XmaxRaw) / 2
    val Ycentroid = (YminRaw + YmaxRaw) / 2

    // Choix du ratio en fonction de l'orientation naturelle de la bbox brute
    val isPaysage = X > Y

    // Forçage du ratio A4 (paysage ou portrait)
    if (isPaysage) {
        Y = X * RATIO_XY_PAYSAGE
        if (Y < coordinates.maxY - coordinates.minY) {
            X = Y * RATIO_XY_PORTRAIT
        }
    } else {
        X = Y * RATIO_XY_PAYSAGE
        if (X < coordinates.maxX - coordinates.minX) {
            Y = X * RATIO_XY_PORTRAIT
        }
    }

    // Respect des dimensions minimales (A4)
    val minX = maxOf(X, if (isPaysage) tailleMinX else tailleMinY)
    val minY = maxOf(Y, if (isPaysage) tailleMinY else tailleMinX)

    X = minX
    Y = minY

    // Recentrage de la bbox après ajustement
    val Xmin = Xcentroid - X / 2
    val Xmax = Xcentroid + X / 2
    val Ymin = Ycentroid - Y / 2
    val Ymax = Ycentroid + Y / 2

    // Orientation réelle après transformation
    val orientation = if (X >= Y) Orientation.PAYSAGE else Orientation.PORTRAIT

    return GeoserveurData(bbox = listOf(Xmin, Ymin, Xmax, Ymax), orientation = orientation)
}

// Calcule les dimensions d'image (WMS) selon le ratio A4 et l'orientation
fun getWMSDimensions(orientation: Orientation, maxSize: Int = 1150): Pair<Int, Int> {
    return when (orientation) {
        Orientation.PORTRAIT -> {
            val height = maxSize
            val width = (height / RATIO_XY_PORTRAIT).toInt()
            width to height
        }
        Orientation.PAYSAGE -> {
            val width = maxSize
            val height = (width * RATIO_XY_PAYSAGE).toInt()
            width to height
        }
    }
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

const val RATIO_XY_PORTRAIT = 1.4142
const val RATIO_XY_PAYSAGE = 0.7070
const val TAILLE_MIN_X_A4_PORTRAIT = 685.0 // correspond au nombre de pixel d'un A4 a 300 dpi (dixit RAI pour remocra v-2)
const val TAILLE_MIN_Y_A4_PORTRAIT = 534.0
const val SCALE_FACTOR = 10 // facteur d'échelle pour convertir les pixels en unités de carte

/**
 * Extrait la largeur réelle d'une image PNG à partir de ses bytes bruts.
 *
 * @param imageBytes Les bytes bruts du fichier PNG
 * @return La largeur de l'image en pixels
 * @throws RemocraResponseException si l'image est invalide ou illisible
 */
fun getImagePNGDimensions(imageBytes: ByteArray?): Int {
    if (imageBytes == null || imageBytes.isEmpty()) {
        throw RemocraResponseException(ErrorType.IMAGE_INVALIDE)
    }

    return try {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes))
            ?: throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
        image.width
    } catch (e: Exception) {
        if (e is RemocraResponseException) throw e
        throw RemocraResponseException(ErrorType.IMAGE_PNG_ILLISIBLE)
    }
}
