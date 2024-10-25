package remocra.web.admin

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object AdminModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(AdminEndpoint::class, NatureEndpoint::class, OrganismeEndpoint::class)
    }
}
