package remocra.api.s

import com.google.inject.Binder
import com.google.inject.Module
import fr.sdis83.remocra.web.s.OpenApiEndpoint
import remocra.web.nomenclatures.NomenclaturesEndpoint
import remocra.web.registerResources

object ApiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(NomenclaturesEndpoint::class)
        binder.registerResources(OpenApiEndpoint::class)
    }
}
