package remocra.web

import jakarta.inject.Inject
import jakarta.ws.rs.core.Application

class JaxrsApplication
@Inject
constructor(private val classes: Set<@JvmSuppressWildcards Class<*>>) : Application() {

    override fun getClasses() = classes
}
