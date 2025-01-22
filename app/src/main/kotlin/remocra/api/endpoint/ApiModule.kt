package remocra.api.endpoint

import com.google.inject.Binder
import com.google.inject.Module
import remocra.apimobile.endpoint.MobileReferentielEndpoint
import remocra.apimobile.endpoint.SynchroEndpoint
import remocra.web.api.OpenApiEndpoint
import remocra.web.registerResources

object ApiModule : Module {
    override fun configure(binder: Binder) {
        // API PEI
        binder.registerResources(OpenApiEndpoint::class)
        binder.registerResources(ApiReferentielsCommunsEndpoint::class)
        binder.registerResources(ApiReferentielsDeciEndpoint::class)
        binder.registerResources(ApiReferentielsPibiEndpoint::class)
        binder.registerResources(ApiReferentielsPenaEndpoint::class)
        binder.registerResources(ApiPeiEndpoint::class)
        binder.registerResources(ApiVisitesEndpoint::class)
        binder.registerResources(ApiIndispoTemporaireEndpoint::class)

        // API Mobile
        binder.registerResources(MobileReferentielEndpoint::class)
        binder.registerResources(SynchroEndpoint::class)
    }
}
