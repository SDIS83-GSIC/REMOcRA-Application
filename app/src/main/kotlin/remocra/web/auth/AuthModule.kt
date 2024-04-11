package remocra.web.auth

import com.google.inject.Binder
import com.google.inject.Module
import remocra.authn.AuthenticationFilter
import remocra.web.registerResources

object AuthModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(AuthenticationFilter::class)
    }
}
