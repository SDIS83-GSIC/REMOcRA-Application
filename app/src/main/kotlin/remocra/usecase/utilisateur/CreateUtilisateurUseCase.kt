package remocra.usecase.utilisateur

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.AuthModule
import remocra.auth.WrappedUserInfo
import remocra.data.UtilisateurData
import remocra.data.enums.ErrorType
import remocra.db.UtilisateurRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.RequiredAction
import remocra.keycloak.representations.UserRepresentation
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class CreateUtilisateurUseCase : AbstractCUDUseCase<UtilisateurData>(TypeOperation.INSERT) {
    @Inject lateinit var keycloakToken: KeycloakToken

    @Inject lateinit var keycloakClient: AuthModule.KeycloakClient

    @Inject lateinit var keycloakApi: KeycloakApi

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    private val logger = LoggerFactory.getLogger(CreateUtilisateurUseCase::class.java)

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
        }
    }

    override fun postEvent(element: UtilisateurData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.utilisateurId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.UTILISATEUR,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: UtilisateurData): UtilisateurData {
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!

        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            // FIXME: ça ne retourne qu'une "page" de résultats, l'utilisateur pourrait exister mais ne pas être retourné
            val listeUtilisateurKeycloak = keycloakApi.getUsers(token).execute().body()

            // Si l'utilisateur n'est pas dans keycloak, on le crée
            if (listeUtilisateurKeycloak?.map { it.username }?.contains(element.utilisateurUsername) != true) {
                val response = keycloakApi.createUser(
                    token,
                    UserRepresentation(
                        id = "",
                        username = element.utilisateurUsername,
                        email = element.utilisateurEmail,
                        lastName = element.utilisateurNom,
                        firstName = element.utilisateurPrenom,
                        requiredActions = listOf(RequiredAction.VERIFY_EMAIL.name),
                    ),
                ).execute()

                if (response.errorBody() != null) {
                    val replacement = "${response.message()} - " +
                        "(${
                            response.errorBody()!!.source()
                        }"
                    throw RemocraResponseException(ErrorType.UTILISATEUR_ERROR_INSERT, replacement)
                }

                logger.info("Utilisateur ${element.utilisateurUsername} inséré dans keycloak")
            }

            val keycloakId = keycloakApi.getUsers(token, username = element.utilisateurUsername, max = 1)
                .execute().body()!!.first().id

            val responseMailKeycloak = keycloakApi.executeActionsEmail(
                token,
                actions = setOf(RequiredAction.VERIFY_EMAIL.name, RequiredAction.UPDATE_PASSWORD.name),
                userId = keycloakId,
                clientId = keycloakClient.clientId,
            ).execute()

            if (!responseMailKeycloak.isSuccessful) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_ACTION_EMAIL)
            }

            utilisateurRepository.insertUtilisateur(
                element.copy(utilisateurId = UUID.randomUUID()),
                keycloakId,
            )

            return element
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UtilisateurData) {
        if (element.utilisateurUsername.trim().length < 3) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH)
        }
        if (utilisateurRepository.checkExistsUsername(element.utilisateurUsername, null)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }
        if (utilisateurRepository.checkExistsEmail(element.utilisateurUsername, null)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_EMAIL_EXISTS)
        }
    }
}
