package remocra.eventbus.mobile

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.apimobile.usecase.ValideIncomingTournee
import remocra.eventbus.EventListener

class IntegrationTourneeEventListener : EventListener<IntegrationTourneeEvent> {
    @Inject
    private lateinit var valideIncomingTournee: ValideIncomingTournee

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onEvent(event: IntegrationTourneeEvent) {
        logger.info("Traitement de la tournée ${event.tourneeId}")
        valideIncomingTournee.execute(event.tourneeId)
        logger.info("Fin de traitement de la tournée ${event.tourneeId}")
    }
}
