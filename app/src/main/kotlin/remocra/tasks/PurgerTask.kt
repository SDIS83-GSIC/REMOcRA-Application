package remocra.tasks

import com.google.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.db.JobRepository
import remocra.db.LogLineRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.usecase.document.DocumentUtils

class PurgerTask : SchedulableTask<PurgerTaskParameter, SchedulableTaskResults>() {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var jobRepository: JobRepository

    @Inject lateinit var logLineRepository: LogLineRepository

    override fun execute(parameters: PurgerTaskParameter?, userInfo: WrappedUserInfo): SchedulableTaskResults? {
        logManager.info("Exécution du job")
        /** Suppression du contenu de documents/tmp/ */
        if (parameters!!.purgerDocumentTemp) {
            logManager.info("[Purge Document Temporaire] Début de la suppression")
            try {
                val dateLimiteDeConservation = dateUtils.now().minusHours(GlobalConstants.DELAI_PURGE_FICHIER_TEMPORAIRE)
                documentUtils.cleanDirectoryFileOlderThan(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE, dateLimiteDeConservation)
                logManager.info("[Purge Document Temporaire] Suppression terminée avec succès")
            } catch (e: Exception) {
                logManager.error("Une erreur est survenue lors de la suppression du contenu de ${GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE} : $e")
            }
        }
        /** Purge des jobs et logLines trop ancien */
        if (parameters.purgerJobTermine) {
            logManager.info("[Purge Logs Jobs] Début de la suppression")
            logManager.info("[Purge Logs Jobs] Récupération des jobs à supprimer")
            val setJobIdToDelete = jobRepository.getIdJobsOlderThanDays(parameters.purgerJobJours!!).toSet()
            logManager.info("[Purge Logs Jobs] Il y a ${setJobIdToDelete.size} jobs datant de plus de ${parameters.purgerJobJours} jours à supprimer")
            logManager.info("[Purge Logs Jobs] Suppression en masse des logLines associées aux jobs")
            logLineRepository.purgeLogLineFromListJobId(setJobIdToDelete)
            logManager.info("[Purge Logs Jobs] Suppression en masse des jobs")
            jobRepository.purgeJobFromSetJobId(setJobIdToDelete)
            logManager.info("[Purge Logs Jobs] Suppression terminée avec succès")
        }
        // TODO : Ajouter la purge des informations concernant le module Adresse/Alerte
        return null
    }

    override fun notifySpecific(executionResults: SchedulableTaskResults?, notificationRaw: NotificationRaw) {
        // Pas de notification pour cette tâche
    }

    override fun checkParameters(parameters: PurgerTaskParameter?) {
        if (parameters == null) {
            throw IllegalArgumentException("Aucun paramètre fourni")
        }
        if (parameters.purgerJobTermine && parameters.purgerJobJours == null) {
            throw IllegalArgumentException("Aucun délai spécifié pour déterminer à partir de quand supprimer les logs")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.PURGER
    }

    override fun getTaskParametersClass(): Class<PurgerTaskParameter> {
        return PurgerTaskParameter::class.java
    }
}

class PurgerTaskParameter(
    override val notification: NotificationMailData?,
    val purgerDocumentTemp: Boolean = false,
    val purgerJobTermine: Boolean = false,
    val purgerJobJours: Long?,
    // val purgerAlerteTermine: Boolean = false,
    // val purgerAlerteJours: Long?,
) : SchedulableTaskParameters(notification)
