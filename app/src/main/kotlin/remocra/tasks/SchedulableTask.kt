package remocra.tasks

/**
 * Classe de base d'une tâche *planifiable*. Pour les tâches à usage unique, utiliser [SimpleTask]
 */
abstract class SchedulableTask<T : SchedulableTaskParameters, U : SchedulableTaskResults> : SimpleTask<T, U>()

open class SchedulableTaskParameters(notificationMail: NotificationMail?) : TaskParameters(notificationMail)
open class SchedulableTaskResults() : JobResults()
