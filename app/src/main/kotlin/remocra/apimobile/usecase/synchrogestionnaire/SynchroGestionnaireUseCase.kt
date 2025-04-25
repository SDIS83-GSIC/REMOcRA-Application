package remocra.apimobile.usecase.synchrogestionnaire

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.GestionnaireRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.incoming.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroGestionnaireUseCase : AbstractCUDUseCase<Gestionnaire>(TypeOperation.INSERT) {
    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var gestionnaireRepository: GestionnaireRepository

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroGestionnaireUseCase::class.java)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.MOBILE_GESTIONNAIRE_C)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_FORBIDDEN)
        }
    }

    override fun postEvent(element: Gestionnaire, userInfo: WrappedUserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: WrappedUserInfo, element: Gestionnaire): Gestionnaire {
        val result: Int =
            incomingRepository.insertGestionnaireIncoming(
                gestionnaireCode = element.gestionnaireCode,
                gestionnaireLibelle = element.gestionnaireLibelle,
                gestionnaireId = element.gestionnaireId,
            )

        when (result) {
            0 -> {
                logger.warn("Le gestionnaire ${element.gestionnaireId} est déjà dans le schéma incoming")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer le gestionnaire ${element.gestionnaireId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_ERROR, element.gestionnaireId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Gestionnaire) {
        // Check si code n'existe pas déjà en base
        if (gestionnaireRepository.checkCodeExists(element.gestionnaireCode, element.gestionnaireId)) {
            throw RemocraResponseException(ErrorType.ADMIN_GESTIONNAIRE_CODE_EXISTS)
        }
    }
}
