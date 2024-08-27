package remocra.web.couverturehydraulique

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object CouvertureHydrauliqueModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(CouvertureHydrauliqueEndPoint::class)
    }
}
