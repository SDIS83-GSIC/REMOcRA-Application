package remocra.web.adresses

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object AdresseModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(AdresseEndpoint::class)
    }
}
