package remocra.eventbus.notification

import remocra.eventbus.Event
import remocra.tasks.NotificationMail
import java.util.UUID

/**
 * Evénement permettant à un traitement de demander une notification asynchrone
 */
class NotificationEvent(val notificationData: NotificationMail, val idJob: UUID) : Event
