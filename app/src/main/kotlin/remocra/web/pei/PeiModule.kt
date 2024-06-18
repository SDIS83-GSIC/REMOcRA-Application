package remocra.web.pei

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object PeiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(PeiEndPoint::class)
    }
}
