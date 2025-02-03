package remocra.web.oldeb

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object OldebModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(
            OldebEndpoint::class,
            CadastreEndpoint::class,
            ProprietaireEndpoint::class,
        )
    }
}
