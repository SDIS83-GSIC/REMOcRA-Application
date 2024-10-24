package remocra.web.marque

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.nature.MarquePibiEndPoint
import remocra.web.registerResources

object MarquePibiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(MarquePibiEndPoint::class)
    }
}
