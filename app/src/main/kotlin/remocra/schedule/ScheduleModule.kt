package remocra.schedule

import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.RemocraModule
import remocra.tasks.ChangementEtatPeiTask
import remocra.tasks.NotifAvantDebutIndispoTempTask
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import remocra.tasks.SchedulableTaskResults
import remocra.tasks.SimpleTask
import remocra.tasks.SynchroUtilisateurTask
import remocra.tasks.TaskParameters

object ScheduleModule : RemocraModule() {
    override fun configure() {
        KotlinMultibinder.newSetBinder<SimpleTask<out TaskParameters, out SchedulableTaskResults>>(kotlinBinder).apply {
            // TODO : ajouter tâches
        }
        // Binder spécifique pour se faire injecter une liste de [SchedulableTask]
        KotlinMultibinder.newSetBinder<SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>>(
            kotlinBinder,
        ).apply {
            // TODO : ajouter tâches programmées
            addBinding().to<SynchroUtilisateurTask>().asEagerSingleton()
            addBinding().to<ChangementEtatPeiTask>().asEagerSingleton()
            addBinding().to<NotifAvantDebutIndispoTempTask>().asEagerSingleton()
        }
    }
}
