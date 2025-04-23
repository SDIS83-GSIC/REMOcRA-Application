package remocra.eventbus.pei

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.data.ModeleMinimalPeiData
import remocra.data.enums.Environment
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
            when (event.typeOperation) {
                TypeOperation.INSERT -> createPei(event)
                TypeOperation.UPDATE -> updatePei(event)
                TypeOperation.DELETE -> deletePei(event)
            }
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
        if (appSettings.environment == Environment.PRODUCTION && appSettings.nexsis.testToken != null) {
            throw IllegalStateException("Impossible d'utiliser un token prédéfini en production, il faut passer par l'a12n NexSIS !")
        }

        // TODO voir comment utiliser la brique d'a12n en production, pour l'instant ce n'est pas possible côté NexSIS, le token est en dur
        val token = appSettings.nexsis.testToken

        val httpClient: HttpClient = HttpClient.newHttpClient()

        val request =
            HttpRequest.newBuilder(URI(appSettings.nexsis.url.plus(endpoint)))
                .let {
                    val ofString = HttpRequest.BodyPublishers.ofString(objectAsString)
                    when (typeOperation) {
                        TypeOperation.INSERT -> it.POST(ofString)
                        TypeOperation.UPDATE -> it.PUT(ofString)
                        TypeOperation.DELETE -> it.DELETE()
                    }
                }
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer $token")
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
            HttpURLConnection.HTTP_NO_CONTENT,
            -> logger.info("Notification NexSIS ok !")

            HttpURLConnection.HTTP_BAD_REQUEST -> logger.error("400 : Bad request")
            HttpURLConnection.HTTP_UNAUTHORIZED -> logger.error("401 : Unauthorized")
            HttpURLConnection.HTTP_FORBIDDEN -> logger.error("403 : Forbidden")
            else -> logger.error("Erreur inconnue : ${response.statusCode()}")
        }
    }
}
