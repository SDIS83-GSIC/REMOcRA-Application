package remocra.web.module

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ModuleModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ModuleEndPoint::class)
    }
}
