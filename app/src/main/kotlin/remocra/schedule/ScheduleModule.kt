package remocra.schedule

import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import remocra.RemocraModule
import remocra.tasks.ApacheHopTask
import remocra.tasks.BasculeAutoIndispoTempTask
import remocra.tasks.ChangementEtatPeiTask
import remocra.tasks.JobResults
import remocra.tasks.NotifAvantDebutIndispoTempTask
import remocra.tasks.NotifAvantFinIndispoTempTask
import remocra.tasks.NotifResteIndispoIndispoTempTask
import remocra.tasks.PurgerTask
import remocra.tasks.RelanceCalculDispoTask
import remocra.tasks.RelanceNumerotationTask
import remocra.tasks.SchedulableTask
import remocra.tasks.SchedulableTaskParameters
import remocra.tasks.SchedulableTaskResults
import remocra.tasks.SimpleTask
import remocra.tasks.SynchroUtilisateurTask
import remocra.tasks.SynchronisationSIGTask
import remocra.tasks.TaskParameters

object ScheduleModule : RemocraModule() {
    override fun configure() {
        KotlinMultibinder.newSetBinder<SimpleTask<out TaskParameters, out JobResults>>(kotlinBinder).apply {
            addBinding().to<RelanceCalculDispoTask>().asEagerSingleton()
            addBinding().to<RelanceNumerotationTask>().asEagerSingleton()
        }

        // Binder spécifique pour se faire injecter une liste de [SchedulableTask]
        KotlinMultibinder.newSetBinder<SchedulableTask<out SchedulableTaskParameters, out SchedulableTaskResults>>(
            kotlinBinder,
        ).apply {
            addBinding().to<SynchroUtilisateurTask>().asEagerSingleton()
            addBinding().to<ChangementEtatPeiTask>().asEagerSingleton()
            addBinding().to<NotifAvantDebutIndispoTempTask>().asEagerSingleton()
            addBinding().to<NotifAvantFinIndispoTempTask>().asEagerSingleton()
            addBinding().to<NotifResteIndispoIndispoTempTask>().asEagerSingleton()
            addBinding().to<BasculeAutoIndispoTempTask>().asEagerSingleton()
            addBinding().to<SynchronisationSIGTask>().asEagerSingleton()
            addBinding().to<PurgerTask>().asEagerSingleton()
        }

        KotlinMultibinder.newSetBinder<ApacheHopTask>(kotlinBinder)
    }
}
