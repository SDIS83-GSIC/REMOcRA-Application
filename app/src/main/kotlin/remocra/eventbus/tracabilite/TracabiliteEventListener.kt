package remocra.eventbus.tracabilite

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.jooq.JSONB
import org.slf4j.LoggerFactory
import remocra.db.TracabiliteRepository
import remocra.db.TransactionManager
import remocra.eventbus.EventListener

class TracabiliteEventListener<T>
@Inject
constructor(
    private val objectMapper: ObjectMapper,
    private val tracabiliteRepository: TracabiliteRepository,
    private val transactionManager: TransactionManager,
) :
    EventListener<TracabiliteEvent<T>> {

    private val logger = LoggerFactory.getLogger(TracabiliteEventListener::class.java)

    @Subscribe
    override fun onEvent(event: TracabiliteEvent<T>) {
        try {
            val result = transactionManager.transactionResult {
                tracabiliteRepository.insertTracabilite(
                    typeOperation = event.typeOperation,
                    typeObjet = event.typeObjet,
                    objetId = event.pojoId,
                    objetData = JSONB.valueOf(objectMapper.writeValueAsString(event.pojo)),
                    auteurId = event.auteurTracabilite.idAuteur,
                    auteurData = JSONB.valueOf(objectMapper.writeValueAsString(event.auteurTracabilite)),
                    date = event.date,
                )
            }

            if (result > 0) {
                logger.info("Insertion dans la tracabilite $event")
            } else {
                logger.error("Erreur lors de l'insertion de la transaction : $event")
            }
        } catch (e: Exception) {
            logger.error("Impossible d'ajouter une ligne dans la traçabilite ($event) : ${e.message}")
        }
    }
}
