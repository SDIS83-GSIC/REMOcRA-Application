package remocra.usecase.api

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.NotificationMailData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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
import remocra.usecase.AbstractCUDUseCase

class RegenereClientKeycloakApiUseCase @Inject constructor(
    private val keycloakApi: KeycloakApi,
    private val organismeRepository: OrganismeRepository,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
) :
    AbstractCUDUseCase<Organisme>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_API)) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_FORBIDDEN)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Organisme) {
        if (element.organismeEmailContact.isNullOrBlank()) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_NULL)
        }

        val existEmail = organismeRepository.fetchEmailExists(element.organismeEmailContact!!, element.organismeId)

        if (existEmail) {
            throw RemocraResponseException(ErrorType.DROIT_API_CLIENT_EMAIL_DOUBLON)
        }
    }

    override fun execute(userInfo: UserInfo?, element: Organisme): Organisme {
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!
        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            val response = keycloakApi.regenereSecret(
                token,
                element.organismeKeycloakId!!,
            ).execute()

            if (!response.isSuccessful) {
                val replacement = "${response.message()} - " +
                    "(${
                        response.errorBody()?.source()
                    }"
                throw RemocraResponseException(ErrorType.DROIT_API_REGENERE_CLIENT_KEYCLOAK, replacement)
            }

            // On poste un Event de notif pour prévenir l'organisme ici, parce qu'on a récupéré le secret
            eventBus.post(
                NotificationEvent(
                    notificationData = NotificationMailData(
                        destinataires = setOf(element.organismeEmailContact!!),
                        objet = "REMOcRA - Connexion API",
                        corps = "Bonjour,<br />Vous trouverez ci-joint votre nouveau mot de passe pour vous connecter à l'API : ${response.body()!!.value} <br />Cordialement,",
                    ),
                    idJob = null,
                ),
            )
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
        return element
    }

    override fun postEvent(element: Organisme, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.organismeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ORGANISME,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }
}
