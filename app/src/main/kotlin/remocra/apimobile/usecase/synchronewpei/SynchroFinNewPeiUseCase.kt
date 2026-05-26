package remocra.apimobile.usecase.synchronewpei

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.WrappedUserInfo
import remocra.eventbus.EventBus
import remocra.eventbus.mobile.IntegrationNewPeiEvent
import remocra.usecase.AbstractUseCase
import java.util.UUID

class SynchroFinNewPeiUseCase
@Inject
constructor(
    private val eventBus: EventBus,
) :
    AbstractUseCase() {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userInfo: WrappedUserInfo, peiId: UUID) {
        logger.info("Fin de la synchronisation des nouveaux PEI")

        eventBus.post(
            IntegrationNewPeiEvent(
                userInfo = userInfo,
                peiId = peiId,
            ),
        )
    }
}
