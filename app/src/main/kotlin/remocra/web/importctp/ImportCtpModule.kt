package remocra.web.importctp

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ImportCtpModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ImportCtpEndpoint::class)
    }
}
