package remocra.tasks

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.importctp.ImportCtpData
import remocra.db.MaterializedViewRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.usecase.importctp.ImportCtpUseCase

/**
 * Tâche d'enregistrement en arrière-plan des visites CTP issues d'un fichier d'import.
 * Intègre chaque visite individuellement avec un log d'avancement et d'erreurs,
 * consultable via l'interface jobs.
 */
class ImportCtpTask @Inject constructor(
    private val importCtpUseCase: ImportCtpUseCase,
    private val visiteRepository: VisiteRepository,
    private val materializedViewRepository: MaterializedViewRepository,
) : SimpleTask<ImportCtpTaskParameters, JobResults>() {

    override fun execute(parameters: ImportCtpTaskParameters?, userInfo: WrappedUserInfo): JobResults {
        requireNotNull(parameters)
        val lignesAvecVisite = parameters.importCtpData.bilanVerifications
            ?.filter { it.dataVisite != null }
            ?: emptyList()
        val total = lignesAvecVisite.size
        logManager.info("Début de l'import CTP : $total visite(s) à intégrer")

        var nbSucces = 0
        var nbErreurs = 0
        try {
            // on désactive les triggers pour éviter le lancer les événements pour chaque insert,
            // et notament de la vue v_pei_visite_date
            // on les lancera à la fin
            visiteRepository.disabledAllTriggerOnVisite()

            lignesAvecVisite.forEachIndexed { index, ligne ->
                val peiRef = "PEI ${ligne.codeInsee}-${ligne.numeroInterne}"
                try {
                    importCtpUseCase.addVisiteFromImportCtp(ligne.dataVisite!!, userInfo)
                    nbSucces++
                    logManager.info("(${index + 1}/$total) Visite intégrée : $peiRef")
                } catch (e: Exception) {
                    nbErreurs++
                    logManager.error("(${index + 1}/$total) Échec d'intégration : $peiRef — ${e.message}")
                }
            }
        } finally {
            visiteRepository.enableAllTriggerOnVisite()
            materializedViewRepository.refreshViewVisites()
        }
        logManager.info("Import CTP terminé : $nbSucces succès, $nbErreurs erreur(s) sur $total visite(s)")
        return JobResults()
    }

    override fun checkParameters(parameters: ImportCtpTaskParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
            throw IllegalArgumentException("Les paramètres de la tâche ne peuvent pas être null")
        }
    }

    override fun getType(): TypeTask = TypeTask.IMPORTER_CTP

    override fun getTaskParametersClass(): Class<ImportCtpTaskParameters> =
        ImportCtpTaskParameters::class.java

    override fun notifySpecific(executionResults: JobResults?, notificationRaw: NotificationRaw) {
        // Pas de notification spécifique pour cette tâche
    }
}

class ImportCtpTaskParameters : TaskParameters(notification = null) {
    // @JsonIgnore : les données d'import ne sont pas persistées dans task_parametres (volume trop important)
    @JsonIgnore
    lateinit var importCtpData: ImportCtpData
}
