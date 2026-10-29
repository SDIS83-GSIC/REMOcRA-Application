package remocra.cli

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.db.OrganismeRepository
import remocra.keycloak.KeycloakApi
import remocra.keycloak.KeycloakToken
import remocra.keycloak.representations.ClientFullScopeAllowedRepresentation

/**
 * Classe pour migrer les clients Keycloak afin de désactiver l'option "fullScopeAllowed".
 *
 * En prévision de la mise à jour en 26.8 de Keycloa, on décoche la case "fullScopeAllowed" pour tous les clients Keycloak créés par REMOcRA.
 * Cela permet d'éviter des warnings dans les logs à chaque fois qu'un token est généré pour un client Keycloak.
 */
class MigrateFullScopeAllowed @Inject constructor(
    private val keycloakApi: KeycloakApi,
    private val keycloakToken: KeycloakToken,
    private val keycloakClient: AuthModule.KeycloakClient,
    private val organismeRepository: OrganismeRepository,
) {

    fun execute() {
        val tokenResponse = keycloakToken
            .getToken(keycloakClient.clientId, keycloakClient.clientSecret)
            .execute()

        check(tokenResponse.isSuccessful) {
            "Impossible de récupérer un jeton Keycloak : ${tokenResponse.code()} ${tokenResponse.message()}"
        }

        val token = requireNotNull(tokenResponse.body()) {
            "Keycloak n'a retourné aucun jeton"
        }

        try {
            val authorization = "${token.tokenType} ${token.accessToken}"

            organismeRepository.getKeycloakClientIds().forEach { keycloakClientId ->
                val response = keycloakApi.updateClientFullScopeAllowed(
                    authorization = authorization,
                    techniqueId = keycloakClientId,
                    client = ClientFullScopeAllowedRepresentation(fullScopeAllowed = false),
                ).execute()

                check(response.isSuccessful) {
                    "Échec de la mise à jour du client $keycloakClientId : " +
                        "${response.code()} ${response.message()} ${response.errorBody()?.string()}"
                }
            }
        } finally {
            keycloakToken.revokeToken(
                token.accessToken,
                keycloakClient.clientId,
                keycloakClient.clientSecret,
            ).execute()
        }
    }
}
