package remocra.apimobile.usecase.synchrogestionnaire

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroContactRoleUseCase : AbstractCUDUseCase<ContactRoleForApiMobileData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroContactRoleUseCase::class.java)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.MOBILE_GESTIONNAIRE_C)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_FORBIDDEN)
        }
    }

    override fun postEvent(element: ContactRoleForApiMobileData, userInfo: UserInfo) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }

    override fun execute(userInfo: UserInfo?, element: ContactRoleForApiMobileData): ContactRoleForApiMobileData {
        val result = incomingRepository.insertContactRole(element.contactId, element.roleId)

        when (result) {
            0 -> {
                logger.warn("Le lien entre le rôle ${element.roleId} et le contact ${element.contactId} est déjà dans le schéma incoming")
            }
            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer le lien entre le rôle ${element.roleId} et le contact ${element.contactId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_CONTACT_ROLE_ERROR)
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: ContactRoleForApiMobileData) {
        // On vérifie que le contact existe bien avant d'ajouter le role / contact
        if (!incomingRepository.checkContactExist(element.contactId)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_GESTIONNAIRE_CONTACT_NO_EXISTE, "contactId = ${element.contactId}, roleId = ${element.roleId}")
        }
    }
}
