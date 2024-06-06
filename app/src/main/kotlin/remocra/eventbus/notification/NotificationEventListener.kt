package remocra.eventbus.notification

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import remocra.eventbus.EventListener

class NotificationEventListener @Inject constructor() :
    EventListener<NotificationEvent> {

    @Subscribe
    override fun onEvent(event: NotificationEvent) {
        TODO("Not yet implemented")
    }
}
