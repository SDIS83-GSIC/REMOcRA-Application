package remocra.eventbus.utilisateur

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import remocra.eventbus.EventListener
import remocra.usecases.utilisateur.UtilisateurOrganismesUseCase

class UtilisateurModifiedEventListener @Inject constructor() :
    EventListener<UtilisateurModifiedEvent> {

    @Inject
    lateinit var utilisateurOrganismesUseCase: UtilisateurOrganismesUseCase

    @Subscribe
    override fun onEvent(event: UtilisateurModifiedEvent) {
        // TODO pour l'instant simplement recalculer les droits de l'utilisateur sur la hiérarchie des organismes
    }
}
