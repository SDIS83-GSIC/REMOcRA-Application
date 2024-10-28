package remocra.web.voie

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object VoieModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(VoieEndPoint::class)
    }
}
