package remocra.eventbus.mobile

import remocra.auth.WrappedUserInfo
import remocra.eventbus.Event
import java.util.UUID

data class IntegrationNewPeiEvent(
    val userInfo: WrappedUserInfo,
    val peiId: UUID,
) : Event
