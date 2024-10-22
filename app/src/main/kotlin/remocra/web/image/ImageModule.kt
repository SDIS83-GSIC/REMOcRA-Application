package remocra.web.image

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ImageModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ImageEndPoint::class)
    }
}
