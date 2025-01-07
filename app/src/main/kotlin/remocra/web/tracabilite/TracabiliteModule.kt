package remocra.web.tracabilite

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object TracabiliteModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(TracabiliteEndpoint::class)
    }
}
