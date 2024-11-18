package remocra.web.debitsimultane

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object DebitSimultaneModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(DebitSimultaneEndpoint::class)
    }
}
