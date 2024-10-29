package remocra.web.thematique

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ThematiqueModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ThematiqueEndpoint::class)
    }
}
