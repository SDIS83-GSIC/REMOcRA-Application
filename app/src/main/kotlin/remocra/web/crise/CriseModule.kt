package remocra.web.crise

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object CriseModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(CriseEndpoint::class)
    }
}
