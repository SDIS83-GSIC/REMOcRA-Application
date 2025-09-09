package remocra.tasks

import jakarta.inject.Inject
import remocra.apimobile.usecase.ValideIncomingTournee
import remocra.auth.WrappedUserInfo
import remocra.db.jooq.remocra.enums.TypeTask
import java.util.UUID

class RelanceIntegrationTourneeTask @Inject constructor(
    private val valideIncomingTournee: ValideIncomingTournee,
) : SimpleTask<RelanceIntegrationTourneeParameters, JobResults>() {

    override fun execute(parameters: RelanceIntegrationTourneeParameters?, userInfo: WrappedUserInfo): JobResults? {
        logManager.info("Traitement de la tournée ${parameters!!.tourneeId}")
        valideIncomingTournee.execute(parameters.tourneeId, userInfo, logManager)
        logManager.info("Fin de traitement de la tournée ${parameters.tourneeId}")

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: RelanceIntegrationTourneeParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.INTEGRER_INCOMING_REMOCRA
    }

    override fun getTaskParametersClass(): Class<RelanceIntegrationTourneeParameters> {
        return RelanceIntegrationTourneeParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class RelanceIntegrationTourneeParameters() : TaskParameters(notification = null) {
    lateinit var tourneeId: UUID
}
