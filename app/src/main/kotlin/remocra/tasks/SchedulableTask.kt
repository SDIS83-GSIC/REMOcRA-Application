package remocra.tasks

import kotlinx.coroutines.Job
import remocra.data.NotificationMailData

/**
 * Classe de base d'une tâche *planifiable*. Pour les tâches à usage unique, utiliser [SimpleTask]
 */
abstract class SchedulableTask<T : SchedulableTaskParameters, U : SchedulableTaskResults> : SimpleTask<T, U>() {
    var schedulableTask: Job? = null
}

open class SchedulableTaskParameters(notificationMailData: NotificationMailData?) : TaskParameters(notificationMailData)
open class SchedulableTaskResults() : JobResults()
