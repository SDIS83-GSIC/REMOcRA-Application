package remocra.eventbus.utilisateur

import com.google.inject.Inject
import remocra.eventbus.Event
import java.util.UUID

/**
 * Evénement déclenché lorsqu'un utilisateur est modifié.
 *
 */
class UtilisateurModifiedEvent @Inject constructor(
    val utilisateurId: UUID,
) : Event
