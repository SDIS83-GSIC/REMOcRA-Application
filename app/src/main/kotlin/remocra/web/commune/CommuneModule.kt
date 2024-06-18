package remocra.web.commune

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object CommuneModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(CommuneEndPoint::class)
    }
}
