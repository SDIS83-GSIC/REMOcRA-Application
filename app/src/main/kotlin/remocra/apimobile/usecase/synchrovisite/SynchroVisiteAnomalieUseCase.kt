package remocra.apimobile.usecase.synchrovisite

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.VisiteAnomalieForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.app.DataCacheProvider
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroVisiteAnomalieUseCase : AbstractCUDUseCase<VisiteAnomalieForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var dataCacheProvider: DataCacheProvider

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroVisiteAnomalieUseCase::class.java)
    }

    override fun checkDroits(userInfo: UserInfo) {
        // Pas de droits particulier aujourd'hui pour faire la synchro !
    }

    override fun postEvent(element: VisiteAnomalieForApiMobileData, userInfo: UserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: UserInfo?, element: VisiteAnomalieForApiMobileData): VisiteAnomalieForApiMobileData {
        val result = incomingRepository.insertVisiteAnomalie(element.visiteId, element.anomalieId)

        when (result) {
            0 -> {
                logger.error("Le lien entre la visite et l'anomalie est déjà dans le schéma incoming : anomalieId: ${element.anomalieId}, visiteId: ${element.visiteId}")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_VISITE_ANOMALIE_EXISTE, "anomalieId: ${element.anomalieId}, visiteId: ${element.visiteId}")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer le lien entre la visite et l'anomalie dans le schéma incoming : anomalieId: ${element.anomalieId}, visiteId: ${element.visiteId}")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_VISITE_ANOMALIE_ERROR, "anomalieId: ${element.anomalieId}, visiteId: ${element.visiteId}")
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: VisiteAnomalieForApiMobileData) {
        // On vérifie si l'anomalie est toujours dans REMOcRA
        if (dataCacheProvider.getAnomalies().values.firstOrNull { it.anomalieId == element.anomalieId } == null) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_VISITE_ANOMALIE_NO_REMOCRA, element.anomalieId.toString())
        }
    }
}
