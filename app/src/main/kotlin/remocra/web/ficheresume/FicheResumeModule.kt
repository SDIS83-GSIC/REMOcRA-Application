package remocra.web.ficheresume

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object FicheResumeModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(FicheResumeEndpoint::class)
    }
}
