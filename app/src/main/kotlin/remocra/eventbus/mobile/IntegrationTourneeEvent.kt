package remocra.eventbus.mobile

import remocra.auth.WrappedUserInfo
import remocra.eventbus.Event
import java.util.UUID

data class IntegrationTourneeEvent(
    val tourneeId: UUID,
    val userInfo: WrappedUserInfo,
) : Event
