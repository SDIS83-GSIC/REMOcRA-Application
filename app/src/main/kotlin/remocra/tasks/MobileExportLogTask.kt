package remocra.tasks

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.db.DocumentRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.mail.MailSettings
import remocra.usecase.document.DocumentUtils
import remocra.utils.DateUtils
import remocra.web.documentTelechargerRessourceFrom
import java.util.UUID
import kotlin.io.path.absolutePathString

class MobileExportLogTask @Inject
constructor(
    private val documentUtils: DocumentUtils,
    private val documentRepository: DocumentRepository,
    private val mailSettings: MailSettings,
) : SimpleTask<MobileExportLogParameters, JobResults>() {

    override fun execute(parameters: MobileExportLogParameters?, userInfo: WrappedUserInfo): JobResults {
        logManager.info("[MOBILE_EXPORT_LOG] - Stockage du fichier de log")
        val nomFichier = "logcat-${dateUtils.format(dateUtils.now(), DateUtils.PATTERN_DATE_ONLY)}.txt"
        val repertoire = GlobalConstants.DOSSIER_MOBILE_LOG
        documentUtils.saveFile(
            parameters!!.fichierLogBytes,
            nomFichier,
            repertoire,
        )

        val idDocument = UUID.randomUUID()
        // On sauvegarde en base
        documentRepository.insertDocument(
            Document(
                documentId = idDocument,
                documentDate = dateUtils.now(),
                documentRepertoire = repertoire.absolutePathString(),
                documentNomFichier = nomFichier,
            ),
        )

        logManager.info("[MOBILE_EXPORT_LOG] - identifiant de la tablette : ${parameters.tabletteId}")
        logManager.info(
            "[MOBILE_EXPORT_LOG] - Lien pour accéder aux logs : ${documentTelechargerRessourceFrom(
                urlSite = mailSettings.urlSite,
                documentId = idDocument,
            )}",
        )

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: MobileExportLogParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.MOBILE_EXPORT_LOG
    }

    override fun getTaskParametersClass(): Class<MobileExportLogParameters> {
        return MobileExportLogParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class MobileExportLogParameters() : TaskParameters(notification = null) {
    lateinit var tabletteId: String

    // Le flux binaire ne doit pas être persiste dans task_parametres.
    @JsonIgnore
    lateinit var fichierLogBytes: ByteArray
}
