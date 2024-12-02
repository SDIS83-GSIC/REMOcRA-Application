package remocra.eventbus.notification

import remocra.eventbus.Event
import remocra.tasks.NotificationMail
import java.util.UUID

/**
 * Evénement permettant de demander une notification asynchrone. 2 cas d'utilisations sont possibles :
 * * depuis un traitement, auquel cas l'ID de job est obligatoire pour gérer les logs et l'état du traitement
 * * depuis l'application, pas d'id de job à fournir
 */
class NotificationEvent(val notificationData: NotificationMail, val idJob: UUID?) : Event
