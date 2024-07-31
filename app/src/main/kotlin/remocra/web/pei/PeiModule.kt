package remocra.web.pei

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources
import remocra.web.tournee.TourneeEndPoint

object PeiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(PeiEndPoint::class, PenaEndPoint::class, TourneeEndPoint::class)
    }
}
