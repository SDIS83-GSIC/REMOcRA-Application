package remocra.web.profildroit

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ProfilDroitModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ProfilDroitEndpoint::class)
    }
}
