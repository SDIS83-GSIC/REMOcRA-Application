package remocra.tasks

import jakarta.inject.Inject
import remocra.apimobile.usecase.ValideIncomingNewPei
import remocra.auth.WrappedUserInfo
import remocra.db.jooq.remocra.enums.TypeTask
import java.util.UUID

class IntegrationNewPeiTask @Inject constructor(
    private val valideIncomingNewPei: ValideIncomingNewPei,
) : SimpleTask<IntegrationNewPeiTaskParameters, JobResults>() {

    override fun execute(parameters: IntegrationNewPeiTaskParameters?, userInfo: WrappedUserInfo): JobResults {
        logManager.info("Traitement des nouveaux PEI")
        valideIncomingNewPei.execute(userInfo, logManager, parameters!!.peiId, transactionManager)
        logManager.info("Fin de traitement des nouveaux PEI")

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: IntegrationNewPeiTaskParameters?) {
        if (parameters == null) {
            logManager.error("Erreur : les paramètres de la tâche sont null")
            throw IllegalArgumentException("Les paramètres de la tâche ne peuvent pas être null")
        }
    }

    override fun getType(): TypeTask {
        return TypeTask.INTEGRER_INCOMING_NEW_PEI_REMOCRA
    }

    override fun getTaskParametersClass(): Class<IntegrationNewPeiTaskParameters> {
        return IntegrationNewPeiTaskParameters::class.java
    }

    override fun notifySpecific(
        executionResults: JobResults?,
        notificationRaw: NotificationRaw,
    ) {
        // Pas de notification spécifique pour cette tâche
    }
}

class IntegrationNewPeiTaskParameters() : TaskParameters(notification = null) {
    lateinit var peiId: UUID
}
