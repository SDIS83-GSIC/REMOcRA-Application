package remocra.tasks

import jakarta.inject.Inject
import remocra.apimobile.usecase.ValideIncomingTournee
import remocra.auth.WrappedUserInfo
import remocra.db.jooq.remocra.enums.TypeTask
import java.util.UUID

/**
 * Tâche de traitement de l'intégration d'une tournée depuis incoming.
 * Cette tâche est déclenchée à la fin de la synchronisation d'une tournée, ou lors de la relance manuelle de l'intégration d'une tournée.
 * Elle valide la tournée dans incoming et effectue les traitements nécessaires pour l'intégrer dans Remocra.
 *
 */
class IntegrationTourneeTask @Inject constructor(
    private val valideIncomingTournee: ValideIncomingTournee,
) : SimpleTask<IntegrationTourneeParameters, JobResults>() {

    override fun execute(parameters: IntegrationTourneeParameters?, userInfo: WrappedUserInfo): JobResults {
        logManager.info("Traitement de la tournée ${parameters!!.tourneeId}")
        valideIncomingTournee.execute(parameters.tourneeId, userInfo, logManager, transactionManager)
        logManager.info("Fin de traitement de la tournée ${parameters.tourneeId}")

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: IntegrationTourneeParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.INTEGRER_INCOMING_REMOCRA
    }

    override fun getTaskParametersClass(): Class<IntegrationTourneeParameters> {
        return IntegrationTourneeParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class IntegrationTourneeParameters() : TaskParameters(notification = null) {
    lateinit var tourneeId: UUID
}
