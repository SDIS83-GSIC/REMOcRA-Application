package remocra.web.profilorganisme

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ProfilOrganismeModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ProfilOrganismeEndPoint::class)
    }
}
