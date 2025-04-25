package remocra.apimobile.usecase.synchrofintournee

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.eventbus.EventBus
import remocra.eventbus.mobile.IntegrationTourneeEvent
import remocra.usecase.AbstractUseCase
import java.util.UUID

class SynchroFinTourneeUseCase : AbstractUseCase() {
    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var eventBus: EventBus

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo) {
        // On met à jour la date de synchronisation de la tournée dans incoming
        incomingRepository.updateDateSynchroFin(dateUtils.now(), tourneeId)

        logger.info("Fin de la synchronisation de la tournée $tourneeId")

        eventBus.post(
            IntegrationTourneeEvent(
                tourneeId = tourneeId,
                userInfo = userInfo,
            ),
        )
    }
}
