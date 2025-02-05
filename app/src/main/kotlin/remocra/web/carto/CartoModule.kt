package remocra.web.carto

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResource

object CartoModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResource<LayersEndpoint>()
        binder.registerResource<GeoserverEndpoint>()
        binder.registerResource<CartoEndpoint>()
    }
}
