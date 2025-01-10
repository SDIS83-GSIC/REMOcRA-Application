package remocra.web.rcci

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object RcciModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(RcciEndpoint::class)
    }
}
