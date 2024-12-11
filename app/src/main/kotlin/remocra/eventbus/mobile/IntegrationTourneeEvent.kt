package remocra.eventbus.mobile

import remocra.auth.UserInfo
import remocra.eventbus.Event
import java.util.UUID

data class IntegrationTourneeEvent(
    val tourneeId: UUID,
    val userInfo: UserInfo,
) : Event
