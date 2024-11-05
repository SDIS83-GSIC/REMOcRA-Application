package remocra.web.naturedeci

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object NatureDeciModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(NatureDeciEndPoint::class)
    }
}
