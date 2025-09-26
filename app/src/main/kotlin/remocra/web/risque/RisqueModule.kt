package remocra.web.risque

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object RisqueModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(RisqueEndPoint::class)
    }
}
