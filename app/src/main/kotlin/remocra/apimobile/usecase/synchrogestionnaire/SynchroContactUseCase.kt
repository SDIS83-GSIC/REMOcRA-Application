package remocra.apimobile.usecase.synchrogestionnaire

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroContactUseCase : AbstractCUDUseCase<ContactForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroContactUseCase::class.java)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.MOBILE_GESTIONNAIRE_C)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_FORBIDDEN)
        }
    }

    override fun postEvent(element: ContactForApiMobileData, userInfo: WrappedUserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: WrappedUserInfo, element: ContactForApiMobileData): ContactForApiMobileData {
        val result = incomingRepository.insertContact(element)

        when (result) {
            0 -> {
                logger.error("Le contact ${element.contactId} est déjà dans le schéma incoming")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer le contact ${element.contactId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_CONTACT_ERROR, element.contactId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ContactForApiMobileData) {
        // On vérifie que le gestionnaire existe bien avant d'ajouter le contact
        if (!incomingRepository.checkGestionnaireExist(element.gestionnaireId)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_CONTACT_NO_EXISTE, "gestionnaireId = ${element.gestionnaireId}, contactId = ${element.contactId}")
        }
    }
}
