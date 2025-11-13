package remocra.eventbus.pei

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.data.ModeleMinimalPeiForNexsisData
import remocra.data.ModeleMinimalPeiForNexsisJsonData
import remocra.data.enums.Environment
import remocra.db.PeiRepository
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.eventbus.EventListener
import remocra.json.NexSisJsonModule
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
    companion object {
        val nexSisObjectMapper: ObjectMapper = jacksonObjectMapper().apply {
            // Conf reprise de l'objectMapper "classique", avec un peu de ménage
            registerModule(Jdk8Module())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())
            registerModule(NexSisJsonModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS)
        }
    }

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var getModeleMinimalPeiUseCase: GetModeleMinimalPeiUseCase

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

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
     * Notifie NexSIS d'un changement effectué sur un PEI (C, U, D).
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

    /**
     * Notifie NexSIS d'une suppression de PEI dans REMOcRA.
     */
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
    private fun createData(peiId: UUID): ModeleMinimalPeiForNexsisJsonData {
        return (getModeleMinimalPeiUseCase.execute(peiId = peiId, forNexsis = true) as ModeleMinimalPeiForNexsisData).convertFormatNexsis()
    }

    /**
     * Notifie NexSIS de la création d'un PEI dans REMOcRA.
     */
    private fun createPei(event: PeiModifiedEvent) {
        val data = createData(event.peiId)
        executeRequest(
            objectAsString = nexSisObjectMapper.writeValueAsString(data),
            endpoint = "/sig/point-eau-incendie",
            typeOperation = event.typeOperation,
        )
    }

    /**
     * Notifie NexSIS de la modification d'un PEI dans REMOcRA.
     */
    private fun updatePei(event: PeiModifiedEvent) {
        val data = createData(event.peiId)

        executeRequest(
            objectAsString = nexSisObjectMapper.writeValueAsString(data),
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
                    val ofString = objectAsString?.let { HttpRequest.BodyPublishers.ofString(objectAsString) }
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

            HttpURLConnection.HTTP_BAD_REQUEST -> logger.error("400 : Bad request - ${response.body()}")
            HttpURLConnection.HTTP_UNAUTHORIZED -> logger.error("401 : Unauthorized")
            HttpURLConnection.HTTP_FORBIDDEN -> logger.error("403 : Forbidden")
            else -> logger.error("Erreur inconnue : ${response.statusCode()}")
        }
    }
}
