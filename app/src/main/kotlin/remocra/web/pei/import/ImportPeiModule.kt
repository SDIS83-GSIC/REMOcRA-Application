package remocra.web.pei.import

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object ImportPeiModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(ImportPeiEndpoint::class)
    }
}
