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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.UUID

/**
 * Cette classe permet de demander la génération d'un courrier en vérifiant les droits de l'utilisateur.
 */
class CourrierGeneratorUseCase
@Inject
constructor(
    private val modeleCourrierRepository: ModeleCourrierRepository,
    private val objectMapper: ObjectMapper,
    private val documentUtils: DocumentUtils,
    private val userInfo: WrappedUserInfo,
    private val transactionManager: TransactionManager,
    private val requestUtils: RequestUtils,
) :
    AbstractUseCase() {

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
    ): UrlCourrier {
        // Vérifier chaque paramètre
        parametreCourrierInput.listParametres?.forEach { param ->
            if (param.estRequis && (param.valeur.isNullOrEmpty())) {
                throw RemocraResponseException(ErrorType.COURRIER_PARAMETRE_NULL, param.nom)
            }
        }

        val pdfPath = executeInternal(parametreCourrierInput, userInfo)

        return UrlCourrier(
            url = uriBuilder
                .queryParam("courrierPath", pdfPath.fileName)
                .build()
                .toString(),
            modeleCourrierId = parametreCourrierInput.modeleCourrierId,
            courrierReference = parametreCourrierInput.courrierReference,
        )
    }

    /**
     * Fonction interne de génération de courrier
     * Retourne un [Path] vers le fichier PDF généré
     *
     */
    fun executeInternal(
        parametreCourrierInput: ParametreCourrierInput,
        userInfo: WrappedUserInfo,
        isApacheHop: Boolean = false,
        mainTransactionManager: TransactionManager? = null,
    ): Path {
        if (!isApacheHop) {
            checkGroupeFonctionnalites(userInfo, parametreCourrierInput.modeleCourrierId)
        }

        return courrierPdfGenerator(
            parametreCourrierInput,
            mainTransactionManager,
        )
    }

    private fun escapeApostrophes(input: String): String = input.replace("'", "''")

    private fun courrierPdfGenerator(
        parameterCourrierInput: ParametreCourrierInput,
        mainTransactionManager: TransactionManager?,
    ): Path {
        val modeleCourrier = modeleCourrierRepository.getModeleCourrier(parameterCourrierInput.modeleCourrierId)

        val mapParameters = (mainTransactionManager ?: transactionManager).transactionResult(
            wrapInsideTransaction = mainTransactionManager == null,
        ) {
            // On va chercher la requête du rapport
            var requete = modeleCourrier.modeleCourrierSourceSql

            // On remplace avec les données paramètres fournies
            parameterCourrierInput.listParametres?.forEach {
                requete = requete.replace(
                    it.nom,
                    escapeApostrophes(it.valeur?.takeIf { valeur -> valeur.isNotBlank() } ?: "null"),
                )
            }

            // On remplace les variables utilisateur de la requête par les données userInfo
            val requeteModifiee = requestUtils.replaceGlobalParameters(userInfo, requete)

            modeleCourrierRepository.executeRequeteSql(requeteModifiee)
                ?: throw RemocraResponseException(ErrorType.COURRIER_GENERATE_NO_DATA_FOUND)
        }.toMutableMap()

        // On ajoute les paramètres système
        mapParameters["dateGeneration"] =
            dateUtils.format(
                dateUtils.now(),
                DateUtils.PATTERN_NATUREL_DATE_ONLY,
            )

        mapParameters["userGenerationCourrier"] = "${userInfo.prenom} ${userInfo.nom}"
        mapParameters["reference"] = parameterCourrierInput.courrierReference

        val report = XDocReportRegistry.getRegistry().loadReport(
            FileInputStream("${modeleCourrier.documentRepertoire}/${modeleCourrier.documentNomFichier}"),
            TemplateEngineKind.Freemarker,
        )

        val context = report.createContext()

        mapParameters.forEach { (key, value) ->
            if (value is JSON) {
                mapParameters[key] = objectMapper.readValue<List<Map<String, Any>>>(value.data())
            }
        }

        context.putMap(mapParameters)

        val nomFichier = "${modeleCourrier.modeleCourrierCode}-${
            dateUtils.format(dateUtils.now(), "yyyy-MM-dd-HH-mm-ss")
        }"

        // On s'assure que le répertoire existe, sinon on le crée
        documentUtils.ensureDirectory(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)

        val pdfPath = GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE.resolve("$nomFichier.pdf")

        val options = Options.getTo(ConverterTypeTo.PDF)
            .via(ConverterTypeVia.ODFDOM)

        FileOutputStream(pdfPath.toFile()).use {
            report.convert(context, options, it)
        }
        return pdfPath
    }

    data class UrlCourrier(
        val url: String,
        val modeleCourrierId: UUID,
        val courrierReference: String,
    )
}
