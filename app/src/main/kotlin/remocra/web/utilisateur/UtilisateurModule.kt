package remocra.web.utilisateur

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object UtilisateurModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(UtilisateurEndpoint::class)
    }
}
