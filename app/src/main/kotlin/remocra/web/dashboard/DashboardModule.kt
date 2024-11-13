package remocra.web.dashboard

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object DashboardModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(DashboardEndPoint::class)
    }
}
