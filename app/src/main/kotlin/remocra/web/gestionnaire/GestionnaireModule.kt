package remocra.web.gestionnaire

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object GestionnaireModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(SiteEndpoint::class, GestionnaireEndpoint::class)
    }
}
