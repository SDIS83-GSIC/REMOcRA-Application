package remocra.eventbus.pei

import com.google.inject.Inject
import remocra.eventbus.Event
import java.util.UUID

/**
 * Evénement déclenché *après* la modification d'un PEI (opérations C, U, D), alors que la BDD est à jour.
 *
 * Cet événement pourra par exemple être écouté par le traitement de "notification de changement d'état d'un PEI"
 */
class PeiModifiedEvent @Inject constructor(
    val peiId: UUID,
) : Event
