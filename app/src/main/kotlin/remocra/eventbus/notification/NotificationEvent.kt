package remocra.eventbus.notification

import remocra.data.NotificationMailData
import remocra.eventbus.Event
import java.util.UUID

/**
 * Evénement permettant de demander une notification asynchrone. 2 cas d'utilisations sont possibles :
 * * depuis un traitement, auquel cas l'ID de job est obligatoire pour gérer les logs et l'état du traitement
 * * depuis l'application, pas d'id de job à fournir
 */
class NotificationEvent(val notificationData: NotificationMailData, val idJob: UUID?) : Event
