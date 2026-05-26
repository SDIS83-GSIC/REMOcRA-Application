package remocra.apimobile.usecase.synchrogestionnaire

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.WrappedUserInfo
import remocra.eventbus.EventBus
import remocra.eventbus.mobile.IntegrationGestionnaireEvent
import remocra.usecase.AbstractUseCase
import java.util.UUID

class SynchroFinGestionnairesUseCase
@Inject
constructor(
    private val eventBus: EventBus,
) :
    AbstractUseCase() {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userInfo: WrappedUserInfo, gestionnaireId: UUID) {
        logger.info("Fin de la synchronisation des gestionnaires")

        eventBus.post(
            IntegrationGestionnaireEvent(
                userInfo = userInfo,
                gestionnaireId = gestionnaireId,
            ),
        )
    }
}
