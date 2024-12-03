package remocra.tasks

import remocra.data.NotificationMailData

/**
 * Classe de base d'une tâche *planifiable*. Pour les tâches à usage unique, utiliser [SimpleTask]
 */
abstract class SchedulableTask<T : SchedulableTaskParameters, U : SchedulableTaskResults> : SimpleTask<T, U>()

open class SchedulableTaskParameters(notificationMailData: NotificationMailData?) : TaskParameters(notificationMailData)
open class SchedulableTaskResults() : JobResults()
