package remocra.web.pei

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources
import remocra.web.tournee.TourneeEndpoint

object PeiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(PeiEndpoint::class, PenaEndpoint::class, PibiEndpoint::class, TourneeEndpoint::class)
    }
}
