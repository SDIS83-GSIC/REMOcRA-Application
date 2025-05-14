package remocra.usecase.courrier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.UriBuilder
import org.jooq.JSON
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.ModeleCourrierRepository
import remocra.db.TransactionManager
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
     * n'est pas dans le bon profil droit ou n'est pas superadmin
     */
    fun checkProfilDroit(userInfo: UserInfo, modeleCourrierId: UUID) {
        if (userInfo.isSuperAdmin) {
            return
        }

        modeleCourrierRepository.checkProfilDroit(modeleCourrierId, userInfo.utilisateurId)
    }

    /** Fonction commune pour la génération de tous les courriers */
    fun execute(
        parametreCourrierInput: ParametreCourrierInput,
        userInfo: UserInfo?,
        uriBuilder: UriBuilder,
    ): UrlCourrier? {
        if (userInfo == null) {
            throw ForbiddenException("Vous ne possédez pas les droits pour générer ce courrier")
        }
        checkProfilDroit(userInfo, parametreCourrierInput.modeleCourrierId)

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

        val filledOdt = File("${GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE}$nomFichier.odt")
        val pdfFile = File("${GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE}$nomFichier.pdf")

        FileOutputStream(filledOdt).use { out ->
            report.process(context, out)
        }

        // génération du pdf
        val process = ProcessBuilder(
            "libreoffice",
            "--headless",
            "--convert-to",
            "pdf",
            "--outdir",
            pdfFile.parent,
            filledOdt.absolutePath,
        ).inheritIO().start()

        val exitCode = process.waitFor()
        if (exitCode == 0 && pdfFile.exists()) {
            return UrlCourrier(
                url = uriBuilder
                    .queryParam("courrierName", Paths.get("$nomFichier.pdf"))
                    .build()
                    .toString(),
                modeleCourrierId = modeleCourrier.modeleCourrierId!!,
                courrierReference = parametreCourrierInput.courrierReference,
            )
        } else {
            throw IllegalArgumentException("Impossible de générer le pdf")
        }
    }

    data class UrlCourrier(
        val url: String,
        val modeleCourrierId: UUID,
        val courrierReference: String,
    )
}
