package remocra.usecase.courrier

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.core.UriBuilder
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperRunManager
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.ModeleCourrierRepository
import remocra.db.TransactionManager
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.DateUtils
import remocra.utils.RequestUtils
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

        if (modeleCourrier.listeDocuments.isNullOrEmpty()) {
            throw IllegalArgumentException("Aucun template n'a été défini pour ce courrier")
        }

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

        if (mapParameters.isNullOrEmpty()) {
            throw IllegalArgumentException("Impossible de récupérer les données pour remplir le template, aucune donnée retournée par la requête")
        }

        val main = modeleCourrier.listeDocuments.find { it.isMainReport }
            ?: throw IllegalArgumentException("Doit avoir un rapport principal")
        val location = "${main.documentRepertoire}/${main.documentNomFichier}"

        val courrier = JasperCompileManager.compileReport(location)

        // On compile ensuite les sous rapports
        if (modeleCourrier.listeDocuments.any { !it.isMainReport }) {
            modeleCourrier.listeDocuments.filter { !it.isMainReport }.forEach {
                mapParameters!![it.documentNomFichier] = JasperCompileManager.compileReport("${it.documentRepertoire}/${it.documentNomFichier}")
            }
        }

        // on ajoute la date
        mapParameters!!["dateGeneration"] = dateUtils.format(dateUtils.now(), DateUtils.Companion.PATTERN_NATUREL_DATE_ONLY)

        // et le nom de l'utilisateur connecté qui génére le courrier
        mapParameters!!["userGenerationCourrier"] = "${userInfo.prenom} ${userInfo.nom}"

        val file = JasperRunManager.runReportToPdf(
            courrier,
            mapParameters,
            JREmptyDataSource(),
        )

        val nomFichier = "${modeleCourrier.modeleCourrierCode}-${
            dateUtils.format(dateUtils.now(), "yyyy-MM-dd-HH-mm-ss")
        }.pdf"

        documentUtils.saveFile(file, nomFichier, GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)
        return UrlCourrier(
            url = uriBuilder
                .queryParam("courrierName", Paths.get(nomFichier))
                .build()
                .toString(),
            modeleCourrierId = modeleCourrier.modeleCourrierId!!,
        )
    }

    data class UrlCourrier(
        val url: String,
        val modeleCourrierId: UUID,
    )
}
