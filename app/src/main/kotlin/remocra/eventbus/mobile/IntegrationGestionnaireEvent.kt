package remocra.eventbus.mobile

import remocra.auth.WrappedUserInfo
import remocra.eventbus.Event
import java.util.UUID

data class IntegrationGestionnaireEvent(
    val userInfo: WrappedUserInfo,
    val gestionnaireId: UUID,
) : Event
