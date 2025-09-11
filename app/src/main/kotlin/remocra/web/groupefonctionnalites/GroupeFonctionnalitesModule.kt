package remocra.web.groupefonctionnalites

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object GroupeFonctionnalitesModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(GroupeFonctionnalitesEndpoint::class)
    }
}
