package remocra.web.admin

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResource

object AdminModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResource(AdminEndpoint::class)
    }
}
