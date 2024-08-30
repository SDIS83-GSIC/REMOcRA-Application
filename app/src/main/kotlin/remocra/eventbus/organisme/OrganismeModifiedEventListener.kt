package remocra.eventbus.organisme

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import remocra.eventbus.EventListener
import remocra.usecases.utilisateur.UtilisateurOrganismesUseCase

class OrganismeModifiedEventListener @Inject constructor() :
    EventListener<OrganismeModifiedEvent> {

    @Inject
    lateinit var utilisateurOrganismesUseCase: UtilisateurOrganismesUseCase

    @Subscribe
    override fun onEvent(event: OrganismeModifiedEvent) {
        // TODO pour l'instant simplement recalculer les droits de l'utilisateur sur la hiérarchie des organismes
    }
}
