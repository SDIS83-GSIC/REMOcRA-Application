package remocra.web.parametres

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ParametreModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ParametreEndpoint::class)
    }
}
