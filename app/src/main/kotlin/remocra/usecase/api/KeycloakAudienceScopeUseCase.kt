package remocra.usecase.api

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.usecase.AbstractUseCase

class KeycloakAudienceScopeUseCase @Inject constructor(
    private val keycloakApi: KeycloakApi,
) : AbstractUseCase() {
    fun associateAudienceScope(token: String, techniqueClientId: String, errorType: ErrorType) {
        val clientScopesResponse = keycloakApi.getClientScopes(token, GlobalConstants.CLIENT_SCOPE_AUDIENCE_REMOCRA).execute()
        if (!clientScopesResponse.isSuccessful) {
            val replacement = "${clientScopesResponse.message()} - (${clientScopesResponse.errorBody()?.source()}"
            throw RemocraResponseException(errorType, replacement)
        }

        val audienceScope = clientScopesResponse.body().orEmpty().firstOrNull { it.name == GlobalConstants.CLIENT_SCOPE_AUDIENCE_REMOCRA }
            ?: throw RemocraResponseException(
                errorType,
                "Client scope '${GlobalConstants.CLIENT_SCOPE_AUDIENCE_REMOCRA}' introuvable dans Keycloak",
            )

        val audienceScopeId = audienceScope.id
            ?: throw RemocraResponseException(
                errorType,
                "Client scope '${GlobalConstants.CLIENT_SCOPE_AUDIENCE_REMOCRA}' sans identifiant technique",
            )

        val defaultScopesResponse = keycloakApi.getDefaultClientScopes(token, techniqueClientId).execute()
        if (!defaultScopesResponse.isSuccessful) {
            val replacement = "${defaultScopesResponse.message()} - (${defaultScopesResponse.errorBody()?.source()}"
            throw RemocraResponseException(errorType, replacement)
        }

        val alreadyAssociated = defaultScopesResponse.body().orEmpty().any { it.id == audienceScopeId }
        if (alreadyAssociated) {
            return
        }

        val addDefaultScopeResponse = keycloakApi.addDefaultClientScope(token, techniqueClientId, audienceScopeId).execute()
        if (!addDefaultScopeResponse.isSuccessful) {
            val replacement = "${addDefaultScopeResponse.message()} - (${addDefaultScopeResponse.errorBody()?.source()}"
            throw RemocraResponseException(errorType, replacement)
        }
    }
}
