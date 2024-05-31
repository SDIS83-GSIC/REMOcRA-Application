package remocra.schedule

import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import remocra.tasks.SimpleTask
import remocra.tasks.TaskParameters

object ScheduleModule : KotlinModule() {
    override fun configure() {
        KotlinMultibinder.newSetBinder<SimpleTask<out TaskParameters>>(kotlinBinder).apply {
            // TODO : ajouter tâches
        }
        // Binder spécifique pour se faire injecter une liste de [SchedulableTask]
        KotlinMultibinder.newSetBinder<SchedulableTask<out SchedulableTaskParameters>>(
            kotlinBinder,
        ).apply {
            // TODO : ajouter tâches programmées
        }
    }
}
