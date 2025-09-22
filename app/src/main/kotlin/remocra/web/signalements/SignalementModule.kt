package remocra.web.signalements

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object SignalementModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(SignalementEndpoint::class)
    }
}
