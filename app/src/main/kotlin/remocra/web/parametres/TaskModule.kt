package remocra.web.parametres

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object TaskModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(TaskEndpoint::class)
    }
}
