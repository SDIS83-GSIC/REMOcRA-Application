package remocra.usecase.courrier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.opensagres.xdocreport.converter.ConverterTypeTo
import fr.opensagres.xdocreport.converter.ConverterTypeVia
import fr.opensagres.xdocreport.converter.Options
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.UriBuilder
import org.jooq.JSON
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.data.enums.ErrorType
import remocra.db.ModeleCourrierRepository
import remocra.db.TransactionManager
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.DateUtils
import remocra.utils.RequestUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.UUID

/**
 * Cette classe permet de demander la génération d'un courrier en vérifiant les droits de l'utilisateur.
 */
class CourrierGeneratorUseCase : AbstractUseCase() {

    @Inject
    lateinit var transactionManager: TransactionManager

    @Inject
    lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    lateinit var requestUtils: RequestUtils

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var objectMapper: ObjectMapper

    /**
     * Vérifie les droits de l'utilisateur, et déclenche une [ForbiddenException] si l'utilisateur
     * n'est pas dans le bon groupe de fonctionnalites ou n'est pas superadmin
     */
    fun checkGroupeFonctionnalites(userInfo: WrappedUserInfo, modeleCourrierId: UUID) {
        if (userInfo.isSuperAdmin) {
            return
        }

        modeleCourrierRepository.checkGroupeFonctionnalites(modeleCourrierId, userInfo.utilisateurId!!)
    }

    /** Fonction commune pour la génération de tous les courriers */
    fun execute(
        parametreCourrierInput: ParametreCourrierInput,
        userInfo: WrappedUserInfo,
        uriBuilder: UriBuilder,
    ): UrlCourrier? {
        checkGroupeFonctionnalites(userInfo, parametreCourrierInput.modeleCourrierId)

        var mapParameters: MutableMap<String, Any?>? = mutableMapOf()

        val modeleCourrier = modeleCourrierRepository.getModeleCourrier(parametreCourrierInput.modeleCourrierId)

        transactionManager.transactionResult {
            // On va chercher la requête du rapport
            var requete = modeleCourrier.modeleCourrierSourceSql

            // On remplace avec les données paramètres fournies
            parametreCourrierInput.listParametres?.forEach {
                requete = requete.replace(
                    it.nom,
                    it.valeur?.takeIf { it.isNotBlank() } ?: "null",
                )
            }

            // On remplace les variables utilisateur de la requête par les données userinfo
            val requeteModifiee = requestUtils.replaceGlobalParameters(userInfo, requete)

            // on exécute la requête et on sauvegarde
            mapParameters = modeleCourrierRepository.executeRequeteSql(requeteModifiee)

            if (mapParameters == null) {
                throw RemocraResponseException(ErrorType.COURRIER_GENERATE_NO_DATA_FOUND)
            }
        }

        // on ajoute la date
        mapParameters!!["dateGeneration"] = dateUtils.format(dateUtils.now(), DateUtils.Companion.PATTERN_NATUREL_DATE_ONLY)

        // et le nom de l'utilisateur connecté qui génére le courrier
        mapParameters!!["userGenerationCourrier"] = "${userInfo.prenom} ${userInfo.nom}"

        // et le nom de l'utilisateur connecté qui génére le courrier
        mapParameters!!["reference"] = parametreCourrierInput.courrierReference
        val report = XDocReportRegistry.getRegistry().loadReport(
            FileInputStream("${modeleCourrier.documentRepertoire}/${modeleCourrier.documentNomFichier}"),
            TemplateEngineKind.Freemarker,
        )

        val context = report.createContext()

        val mapTemp = mapParameters
        mapTemp!!.forEach {
            (it.value as? JSON)?.let { it1 -> mapTemp[it.key] = objectMapper.readValue<List<Map<String, Any>>>(it1.data()) }
        }

        mapParameters = mapTemp

        context.putMap(mapParameters)

        val nomFichier = "${modeleCourrier.modeleCourrierCode}-${
            dateUtils.format(dateUtils.now(), "yyyy-MM-dd-HH-mm-ss")
        }"

        // On s'assure que le répertoire existe, sinon on le crée
        documentUtils.ensureDirectory(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)

        val pdfFile = File("${GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE}$nomFichier.pdf")

        val options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM)

        FileOutputStream(pdfFile).use {
            report.convert(context, options, it)
        }

        return UrlCourrier(
            url = uriBuilder
                .queryParam("courrierName", Paths.get("$nomFichier.pdf"))
                .build()
                .toString(),
            modeleCourrierId = modeleCourrier.modeleCourrierId!!,
            courrierReference = parametreCourrierInput.courrierReference,
        )
    }

    data class UrlCourrier(
        val url: String,
        val modeleCourrierId: UUID,
        val courrierReference: String,
    )
}
