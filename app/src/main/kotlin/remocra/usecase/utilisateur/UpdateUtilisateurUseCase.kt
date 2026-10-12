package remocra.usecase.utilisateur

import jakarta.inject.Inject
import remocra.app.UsernameLengthConstraints
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
import java.io.IOException

class UpdateUtilisateurUseCase
@Inject
constructor(
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val keycloakApi: KeycloakApi,
    private val utilisateurRepository: UtilisateurRepository,
    private val usernameLengthConstraints: UsernameLengthConstraints,
) :
    AbstractCUDUseCase<UtilisateurData>(TypeOperation.UPDATE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A) &&
            !userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_ORGA_A)
        ) {
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
        // Si l'utilisateur provient du LDAP, inutile de rappeler Keycloak
        if (!element.utilisateurFromLdap) {
            val tokenResponse = try {
                keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()
                    ?: throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK)
            } catch (e: IOException) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK, "${e.message}")
            }

            try {
                val keycloakId = utilisateurRepository.getKeycloakId(element.utilisateurId)
                val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

                // Mise à jour l'utilisateur côté Keycloak
                val updateResponse = try {
                    keycloakApi.updateUser(
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
                } catch (e: IOException) {
                    throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK, "${e.message}")
                }

                if (!updateResponse.isSuccessful) {
                    throw RemocraResponseException(ErrorType.UTILISATEUR_MAJ_KEYCLOAK, updateResponse.errorBody()?.string())
                }
            } finally {
                try {
                    keycloakToken.revokeToken(tokenResponse.accessToken, keycloakClient.clientId, keycloakClient.clientSecret).execute()
                } catch (_: IOException) {
                    // Keycloak déjà injoignable à ce stade : pas la peine de relancer une erreur sur la révocation du token
                }
            }
        }

        // Mise à jour côté REMOcRA
        utilisateurRepository.updateUtilisateur(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UtilisateurData) {
        if (element.utilisateurUsername.trim().length !in usernameLengthConstraints.min..usernameLengthConstraints.max) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_LENGTH, "${usernameLengthConstraints.min} et maximum ${usernameLengthConstraints.max}")
        }

        if (utilisateurRepository.checkExistsUsername(element.utilisateurUsername, element.utilisateurId)) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_USERNAME_EXISTS)
        }

        if (!userInfo.isSuperAdmin && !userInfo.hasDroit(droitWeb = Droit.ADMIN_UTILISATEURS_A)) {
            if (!(userInfo.affiliatedOrganismeIds?.contains(element.utilisateurOrganismeId) ?: false)) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_FORBIDDEN)
            }
        }
    }
}
