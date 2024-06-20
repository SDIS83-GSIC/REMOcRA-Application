package remocra.api.endpoint

import com.google.inject.Binder
import com.google.inject.Module
import fr.sdis83.remocra.web.s.OpenApiEndpoint
import remocra.web.registerResources

object ApiModule : Module {
    override fun configure(binder: Binder) {
        // API PEI
        binder.registerResources(OpenApiEndpoint::class)
        binder.registerResources(ApiReferentielsCommunsEndpoint::class)
        binder.registerResources(ApiReferentielsDeciEndpoint::class)
        binder.registerResources(ApiReferentielsPibiEndpoint::class)
        binder.registerResources(ApiReferentielsPenaEndpoint::class)
    }
}
