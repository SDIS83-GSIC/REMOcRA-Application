package remocra.eventbus.mobile

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.tasks.IntegrationNewPeiTask
import remocra.tasks.IntegrationNewPeiTaskParameters

class IntegrationNewPeiEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val taskProvider: Provider<IntegrationNewPeiTask>,
) : EventListener<IntegrationNewPeiEvent> {
    @Subscribe
    override fun onEvent(event: IntegrationNewPeiEvent) {
        taskProvider.get().start(
            logManager = logManagerFactory.create(),
            event.userInfo,
            IntegrationNewPeiTaskParameters().apply {
                peiId = event.peiId
            },
        )
    }
}
