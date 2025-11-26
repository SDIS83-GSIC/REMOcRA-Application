package remocra.usecase.utilisateur

import jakarta.inject.Inject
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
import remocra.keycloak.representations.UserRepresentation
import remocra.usecase.AbstractCUDUseCase

class UpdateUtilisateurUseCase : AbstractCUDUseCase<UtilisateurData>(TypeOperation.UPDATE) {
    @Inject lateinit var keycloakToken: KeycloakToken

    @Inject lateinit var keycloakClient: AuthModule.KeycloakClient

    @Inject lateinit var keycloakApi: KeycloakApi

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

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
            val keycloakId = utilisateurRepository.getKeycloakId(element.utilisateurId)
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            // Mise à jour l'utilisateur côté Keycloak
            val updateResponse = keycloakApi.updateUser(
                token,
                user = UserRepresentation(
                    id = keycloakId,
                    username = element.utilisateurUsername,
                    firstName = element.utilisateurPrenom,
                    lastName = element.utilisateurNom,
                    email = element.utilisateurEmail,
                    enabled = element.utilisateurActif,
                    requiredActions = listOf(),
                ),
                userId = keycloakId,
            ).execute()

            if (!updateResponse.isSuccessful) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK)
            }

            // Mise à jour côté REMOcRA
            utilisateurRepository.updateUtilisateur(element)
            return element
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UtilisateurData) {
        if (element.utilisateurUsername.trim().length < 3) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH)
        }

        if (utilisateurRepository.checkExistsUsername(element.utilisateurUsername, element.utilisateurId)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }
    }
}
