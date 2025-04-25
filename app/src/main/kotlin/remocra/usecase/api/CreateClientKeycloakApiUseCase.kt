package remocra.usecase.api

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.auth.WrappedUserInfo
import remocra.data.NotificationMailData
import remocra.data.enums.ErrorType
import remocra.db.OrganismeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.eventbus.notification.NotificationEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.ClientRepresentation
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class CreateClientKeycloakApiUseCase @Inject constructor(
    private val keycloakApi: KeycloakApi,
    private val organismeRepository: OrganismeRepository,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
) :
    AbstractCUDUseCase<Organisme>(TypeOperation.INSERT) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(Droit.ADMIN_API)) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Organisme) {
        if (element.organismeEmailContact.isNullOrBlank()) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_NULL)
        }

        val existEmail = organismeRepository.fetchEmailExists(element.organismeEmailContact!!, element.organismeId)

        if (existEmail) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_DOUBLON)
        }

        if (element.organismeKeycloakId != null) {
            throw RemocraResponseException(ErrorType.DROIT_API_DEJA_CLIENT_KEYCLOAK)
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: Organisme): Organisme {
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!
        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            val idTechnique = UUID.randomUUID().toString()

            val response = keycloakApi.createClient(
                token,
                ClientRepresentation(
                    id = idTechnique,
                    clientId = element.organismeEmailContact!!, // l'email n'est pas nulle, on l'a vérifié dans le checkContraintes
                    name = element.organismeCode,
                    secret = null,
                ),
            ).execute()

            if (!response.isSuccessful) {
                val replacement = "${response.message()} - " +
                    "(${
                        response.errorBody()?.source()
                    }"
                throw RemocraResponseException(ErrorType.DROIT_API_INSERT_CLIENT_KEYCLOAK, replacement)
            }

            // On update l'organisme
            organismeRepository.updateKeycloakClientId(idTechnique, element.organismeId)
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
        return element
    }

    override fun postEvent(element: Organisme, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.organismeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ORGANISME,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!
        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"
            // on demande à Keycloak le client secret pour pouvoir l'envoyer à l'organisme
            val credentialRepresentation = keycloakApi.regenereSecret(
                token,
                organismeRepository.getById(element.organismeId).organismeKeycloakId!!,
            ).execute().body()

            // On poste un Event de notif pour prévenir l'utilisateur
            eventBus.post(
                NotificationEvent(
                    notificationData = NotificationMailData(
                        destinataires = setOf(element.organismeEmailContact!!),
                        objet = "REMOcRA - Connexion API",
                        corps = "Bonjour,<br />Vous trouverez ci-joint votre mot de passe pour vous connecter à l'API : ${credentialRepresentation!!.value} <br />Cordialement,",
                    ),
                    idJob = null,
                ),
            )
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
    }
}
