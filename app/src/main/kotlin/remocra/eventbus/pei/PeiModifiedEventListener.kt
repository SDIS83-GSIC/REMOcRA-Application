package remocra.eventbus.pei

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.data.ModeleMinimalPeiData
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.eventbus.EventListener
import remocra.usecase.modeleminimalpei.GetModeleMinimalPeiUseCase
import remocra.utils.DateUtils
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

/**
 * EventListener écoutant les modifications effectuées sur une PEI hors schéma "classique" (traçabilité par exemple)
 *
 */
class PeiModifiedEventListener @Inject constructor() :
    EventListener<PeiModifiedEvent> {

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var getModeleMinimalPeiUseCase: GetModeleMinimalPeiUseCase

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var dateUtils: DateUtils

    private val logger = LoggerFactory.getLogger(PeiModifiedEventListener::class.java)

    @Inject
    private lateinit var appSettings: AppSettings

    @Subscribe
    override fun onEvent(event: PeiModifiedEvent) {
        notifyNexSIS(event)
    }

    /**
     * Notifie NexSIS d'un changement effectué sur un PEI.
     * Il est possible, presque certain, que seul un changement de dispo nécessite une notification, à voir / affiner
     */
    private fun notifyNexSIS(event: PeiModifiedEvent) {
        if (appSettings.nexsis.enabled) {
            // Le code structure est forcément rempli si l'option enabled est positionnée
            val codeStructure = appSettings.nexsis.codeStructure!!

            when (event.typeOperation) {
                TypeOperation.INSERT -> createPei(event)
                TypeOperation.UPDATE -> updatePei(event)
                TypeOperation.DELETE -> deletePei(event)
            }

            // TODO stocker les appels / réponses à NexSIS, ne serait-ce qu'en logs d'une tâche ou autre ?
        }
    }

    private fun deletePei(event: PeiModifiedEvent) {
        executeRequest(
            objectAsString = null,
            endpoint = "/sig/point-eau-incendie/${event.peiId}",
            typeOperation = event.typeOperation,
        )
    }

    /**
     * Crée l'objet data identique commun à la création et la modification.
     */
    private fun createData(peiId: UUID): ModeleMinimalPeiData {
        return getModeleMinimalPeiUseCase.execute(peiId)
    }

    private fun createPei(event: PeiModifiedEvent) {
        val data = createData(event.peiId)
        executeRequest(
            objectAsString = objectMapper.writeValueAsString(data),
            endpoint = "/sig/point-eau-incendie",
            typeOperation = event.typeOperation,
        )
    }

    private fun updatePei(event: PeiModifiedEvent) {
        val data = createData(event.peiId)
        executeRequest(
            objectAsString = objectMapper.writeValueAsString(data),
            endpoint = "/sig/point-eau-incendie/${event.peiId}",
            typeOperation = event.typeOperation,
        )
    }

    /**
     * Exécute la requête HTTP vers NexSIS, en fonction du Endpoint souhaité
     */
    private fun executeRequest(objectAsString: String?, endpoint: String, typeOperation: TypeOperation) {
        val httpClient: HttpClient = HttpClient.newHttpClient()

        val request =
            HttpRequest.newBuilder(URI(appSettings.nexsis.url).resolve(URI("./$endpoint")))
                .let {
                    val ofString = HttpRequest.BodyPublishers.ofString(objectAsString)
                    when (typeOperation) {
                        TypeOperation.INSERT -> it.POST(ofString)
                        TypeOperation.UPDATE -> it.PUT(ofString)
                        TypeOperation.DELETE -> it.DELETE()
                    }
                }
                .setHeader("Content-Type", "application/json")
//                        .setHeader(key, value)
                .build()
        if (appSettings.nexsis.mock) {
            logger.debug("Appel à l'API NexSIS : {}", request)
            return
        }
        val response =
            try {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            } catch (e: InterruptedException) {
                logger.error("Erreur lors de la communication avec le serveur ", e)
                Thread.currentThread().interrupt()
                throw IllegalStateException(e.message)
            } catch (e: Exception) {
                logger.error("Erreur lors de la communication avec le serveur ", e)
                throw IllegalStateException(e.message)
            }

        when (response.statusCode()) {
            HttpURLConnection.HTTP_OK,
            HttpURLConnection.HTTP_CREATED,
            -> TODO("success")

            HttpURLConnection.HTTP_BAD_REQUEST -> TODO("")
            HttpURLConnection.HTTP_UNAUTHORIZED -> TODO("")
            HttpURLConnection.HTTP_FORBIDDEN -> TODO("")
        }
    }
}
