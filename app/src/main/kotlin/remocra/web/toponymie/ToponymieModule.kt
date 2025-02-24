package remocra.web.toponymie

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ToponymieModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ToponymieEndpoint::class)
    }
}
