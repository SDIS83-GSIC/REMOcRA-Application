package remocra.web.courrier

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object CourrierModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(CourrierEndPoint::class)
    }
}
