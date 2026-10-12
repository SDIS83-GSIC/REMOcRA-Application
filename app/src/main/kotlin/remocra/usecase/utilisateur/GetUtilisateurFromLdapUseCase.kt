package remocra.usecase.utilisateur

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.data.enums.ErrorType
import remocra.db.UtilisateurRepository
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetUtilisateurFromLdapUseCase
@Inject
constructor(
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val keycloakApi: KeycloakApi,
    private val utilisateurRepository: UtilisateurRepository,
) : AbstractUseCase() {
    fun execute(utilisateurId: UUID): Boolean {
        val utilisateur = utilisateurRepository.getById(utilisateurId)
        val tokenResponse = keycloakToken.getToken(keycloakClient.clientId, keycloakClient.clientSecret).execute().body()
            ?: throw RemocraResponseException(ErrorType.UTILISATEUR_VERIFICATION_KEYCLOAK)
        val token = "${tokenResponse.tokenType} ${tokenResponse.accessToken}"

        return try {
            val utilisateurKeycloak = keycloakApi.getUsersByUsername(token, utilisateur.utilisateurUsername).execute().body()
            utilisateurKeycloak?.firstOrNull()?.federationLink != null
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_VERIFICATION_KEYCLOAK, e.message)
        }
    }
}
