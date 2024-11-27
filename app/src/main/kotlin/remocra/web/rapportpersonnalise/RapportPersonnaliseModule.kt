package remocra.web.rapportpersonnalise

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object RapportPersonnaliseModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(RapportPersonnaliseEndpoint::class)
    }
}
