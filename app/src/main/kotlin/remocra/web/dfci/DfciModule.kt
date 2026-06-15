package remocra.web.dfci

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object DfciModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(
            DfciEndpoint::class,
            DfciAiresEndpoint::class,
            DfciPistesEndpoint::class,
            DfciDebEndpoint::class,
            DfciPanneauxEndpoint::class,
        )
    }
}
