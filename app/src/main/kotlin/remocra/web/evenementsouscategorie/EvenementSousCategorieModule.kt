package remocra.web.evenementsouscategorie

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object EvenementSousCategorieModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(EvenementSousCategorieEndPoint::class)
    }
}
