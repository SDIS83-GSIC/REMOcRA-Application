package remocra.apimobile.usecase.synchrovisite

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.VisiteForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroVisiteUseCase : AbstractCUDUseCase<VisiteForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var peiRepository: PeiRepository

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroVisiteUseCase::class.java)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        // Pas de droits particulier aujourd'hui pour faire la synchro !
    }

    override fun postEvent(element: VisiteForApiMobileData, userInfo: WrappedUserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: WrappedUserInfo, element: VisiteForApiMobileData): VisiteForApiMobileData {
        // On insère la visite
        val result = incomingRepository.insertVisite(element, dateUtils.getMoment(element.visiteDate))

        // Puis si c'est un ctrl débit pression, on l'insère
        if (element.ctrDebitPression) {
            incomingRepository.insertVisiteCtrlDebitPression(element)
        }

        when (result) {
            0 -> {
                logger.warn("La visite ${element.visiteId} est déjà dans le schéma incoming")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer la visite ${element.visiteId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_VISITE_ERROR, element.visiteId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: VisiteForApiMobileData) {
        // On check le format de la date, s'il n'est pas bon, une erreur est catchée
        dateUtils.getMomentForResponse(element.visiteDate)

        // On vérifie si le PEI est toujours dans REMOcRA
        if (!peiRepository.checkExists(element.peiId)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_VISITE_PEI_NO_REMOCRA, element.peiId.toString())
        }
    }
}
