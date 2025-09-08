package remocra.usecase.tournee

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.eventbus.EventBus
import remocra.eventbus.mobile.IntegrationTourneeEvent
import remocra.usecase.AbstractUseCase
import java.util.UUID

class RelancerIntegrationTourneeIncomingUseCase @Inject constructor(
    private val eventBus: EventBus,
) : AbstractUseCase() {

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo) {
        eventBus.post(
            IntegrationTourneeEvent(
                tourneeId = tourneeId,
                userInfo = userInfo,
            ),
        )
    }
}
