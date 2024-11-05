package remocra.web.typeorganisme

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object TypeOrganismeModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(TypeOrganismeEndPoint::class)
    }
}
