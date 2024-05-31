package remocra.tasks

/**
 * Classe de base d'une tâche *planifiable*. Pour les tâches à usage unique, utiliser [SimpleTask]
 */
abstract class SchedulableTask<T : SchedulableTaskParameters> : SimpleTask<T>()

open class SchedulableTaskParameters(notificationMail: NotificationMail?) : TaskParameters(notificationMail)
