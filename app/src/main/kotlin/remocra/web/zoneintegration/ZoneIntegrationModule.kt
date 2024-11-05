package remocra.web.zoneintegration

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ZoneIntegrationModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ZoneIntegrationEndPoint::class)
    }
}
