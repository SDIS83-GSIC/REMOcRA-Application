package remocra.web.evenementcategorie

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object EvenementCategorieModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(EvenementCategorieEndpoint::class)
    }
}
