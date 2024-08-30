package remocra.eventbus.organisme

import com.google.inject.Inject
import remocra.eventbus.Event
import java.util.UUID

/**
 * Evénement déclenché lorsqu'un organisme est modifié. Ca peut avoir un impact sur les droits des utilisateurs rattachés à cet organisme sur les enfants de celui-ci
 *
 */
class OrganismeModifiedEvent @Inject constructor(
    val organismeId: UUID,
) : Event
