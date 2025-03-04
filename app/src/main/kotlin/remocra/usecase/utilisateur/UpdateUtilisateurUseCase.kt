package remocra.usecase.utilisateur

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.UtilisateurData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.UtilisateurRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
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

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_UTILISATEURS_A)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
        }
    }

    override fun postEvent(element: UtilisateurData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.utilisateurId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.UTILISATEUR,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: UtilisateurData): UtilisateurData {
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()!!

        try {
            val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

            // Mise à jour l'utilisateur côté Keycloak
            val updateResponse = keycloakApi.updateUser(
                token,
                user = UserRepresentation(
                    id = element.utilisateurId.toString(),
                    username = element.utilisateurUsername,
                    firstName = element.utilisateurPrenom,
                    lastName = element.utilisateurNom,
                    email = element.utilisateurEmail,
                    enabled = element.utilisateurActif,
                    requiredActions = listOf(),
                ),
                userId = element.utilisateurId.toString(),
            ).execute()

            if (!updateResponse.isSuccessful) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK)
            }

            // Mise à jour côté REMOcRA
            utilisateurRepository.updateUtilisateur(
                Utilisateur(
                    utilisateurId = element.utilisateurId,
                    utilisateurActif = element.utilisateurActif,
                    utilisateurEmail = element.utilisateurEmail,
                    utilisateurNom = element.utilisateurNom,
                    utilisateurPrenom = element.utilisateurPrenom,
                    utilisateurUsername = element.utilisateurUsername,
                    utilisateurTelephone = element.utilisateurTelephone,
                    utilisateurCanBeNotified = element.utilisateurCanBeNotified,
                    utilisateurProfilUtilisateurId = element.utilisateurProfilUtilisateurId,
                    utilisateurOrganismeId = element.utilisateurOrganismeId,
                    utilisateurIsSuperAdmin = element.utilisateurIsSuperAdmin,
                ),
            )

            return element
        } finally {
            keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: UtilisateurData) {
        if (element.utilisateurUsername.trim().length < 3) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH)
        }

        if (utilisateurRepository.checkExistsUsername(element.utilisateurUsername, element.utilisateurId)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }

        if (utilisateurRepository.checkExistsEmail(element.utilisateurUsername, element.utilisateurId)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_EMAIL_EXISTS)
        }
    }
}
