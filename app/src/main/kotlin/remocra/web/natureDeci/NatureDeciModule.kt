package remocra.web.natureDeci

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object NatureDeciModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(NatureDeciEndPoint::class)
    }
}
