package remocra.apimobile.usecase.synchrotournee

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.TourneeSynchroForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroTourneeUseCase : AbstractCUDUseCase<TourneeSynchroForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroTourneeUseCase::class.java)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Pas de droits particulier aujourd'hui pour faire la synchro !
    }

    override fun postEvent(element: TourneeSynchroForApiMobileData, userInfo: WrappedUserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: WrappedUserInfo, element: TourneeSynchroForApiMobileData): TourneeSynchroForApiMobileData {
        val result = incomingRepository.insertTournee(element)

        when (result) {
            0 -> {
                logger.warn("La tournée ${element.tourneeId} est déjà dans le schéma incoming")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer la tournée ${element.tourneeId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_TOURNEE_ERROR, element.tourneeId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: TourneeSynchroForApiMobileData) {
        // Pas de contraintes
    }
}
