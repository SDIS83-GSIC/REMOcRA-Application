package remocra.eventbus.mobile

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.tasks.IntegrationGestionnairesTask
import remocra.tasks.IntegrationGestionnairesTaskParameters

class IntegrationGestionnaireEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val taskProvider: Provider<IntegrationGestionnairesTask>,
) : EventListener<IntegrationGestionnaireEvent> {
    @Subscribe
    override fun onEvent(event: IntegrationGestionnaireEvent) {
        taskProvider.get().start(
            logManager = logManagerFactory.create(),
            event.userInfo,
            IntegrationGestionnairesTaskParameters().apply {
                gestionnaireId = event.gestionnaireId
            },
        )
    }
}
