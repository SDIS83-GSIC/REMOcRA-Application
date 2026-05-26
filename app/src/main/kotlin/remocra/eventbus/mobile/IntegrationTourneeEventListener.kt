package remocra.eventbus.mobile

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.tasks.IntegrationTourneeParameters
import remocra.tasks.IntegrationTourneeTask

class IntegrationTourneeEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val taskProvider: Provider<IntegrationTourneeTask>,
) : EventListener<IntegrationTourneeEvent> {
    @Subscribe
    override fun onEvent(event: IntegrationTourneeEvent) {
        taskProvider.get().start(
            logManager = logManagerFactory.create(),
            event.userInfo,
            IntegrationTourneeParameters().apply {
                this.tourneeId = event.tourneeId
            },
        )
    }
}
