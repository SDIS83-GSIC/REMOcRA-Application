package remocra.eventbus.mobile

import remocra.eventbus.Event
import java.util.UUID

data class IntegrationTourneeEvent(
    val tourneeId: UUID,
) : Event
