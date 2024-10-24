package remocra.web.organisme

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object OrganismeModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(OrganismeEndPoint::class, TypeOrganismeEndPoint::class)
    }
}
