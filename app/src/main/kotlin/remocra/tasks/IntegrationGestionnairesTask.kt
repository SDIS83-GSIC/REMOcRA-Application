package remocra.tasks

import jakarta.inject.Inject
import remocra.apimobile.usecase.ValideIncomingGestionnairesUseCase
import remocra.auth.WrappedUserInfo
import remocra.db.jooq.remocra.enums.TypeTask
import java.util.UUID

class IntegrationGestionnairesTask @Inject constructor(
    private val valideGestionnaireUseCase: ValideIncomingGestionnairesUseCase,
) : SimpleTask<IntegrationGestionnairesTaskParameters, JobResults>() {

    override fun execute(parameters: IntegrationGestionnairesTaskParameters?, userInfo: WrappedUserInfo): JobResults {
        logManager.info("Traitement des gestionnaires")

        valideGestionnaireUseCase.execute(userInfo, logManager, parameters!!.gestionnaireId, transactionManager)
        logManager.info("Fin de traitement des gestionnaires")

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: IntegrationGestionnairesTaskParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
            throw IllegalArgumentException("Les paramètres de la tâche ne peuvent pas être null")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.INTEGRER_INCOMING_GESTIONNAIRE_REMOCRA
    }

    override fun getTaskParametersClass(): Class<IntegrationGestionnairesTaskParameters> {
        return IntegrationGestionnairesTaskParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class IntegrationGestionnairesTaskParameters : TaskParameters(notification = null) {
    lateinit var gestionnaireId: UUID
}
