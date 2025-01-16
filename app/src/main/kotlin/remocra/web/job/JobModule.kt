package remocra.web.job

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object JobModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(JobEndpoint::class)
    }
}
