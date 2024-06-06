package remocra.eventbus

import jakarta.inject.Inject

class EventBusImpl
@Inject
constructor(
    private val eventBus: com.google.common.eventbus.EventBus,
    listeners: Set<EventListener<*>>,
) : EventBus {

    init {
        listeners.forEach { eventBus.register(it) }
    }

    override fun post(event: Event) {
        eventBus.post(event)
    }
}
