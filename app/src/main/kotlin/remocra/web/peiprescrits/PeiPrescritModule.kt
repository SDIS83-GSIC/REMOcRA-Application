package remocra.web.peiprescrits

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object PeiPrescritModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(PeiPrescritsEndPoint::class)
    }
}
