package remocra.eventbus.notification

import remocra.eventbus.Event
import java.util.UUID

/**
 * Evénement permettant à un traitement de demander une notification asynchrone
 */
class NotificationEvent(val idJob: UUID) : Event
