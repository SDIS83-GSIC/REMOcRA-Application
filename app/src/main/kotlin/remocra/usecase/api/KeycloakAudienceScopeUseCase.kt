package remocra.usecase.api

import jakarta.inject.Inject
import remocra.auth.AuthModule
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import remocra.keycloak.KeycloakApi
import remocra.usecase.AbstractUseCase

class KeycloakAudienceScopeUseCase @Inject constructor(
    private val keycloakApi: KeycloakApi,
    private val authnSettings: AuthModule.AuthnSettings,
) : AbstractUseCase() {
    fun associateAudienceScope(token: String, techniqueClientId: String, errorType: ErrorType) {
        val clientScopesResponse = keycloakApi.getClientScopes(token, authnSettings.clientScopeApiAudience).execute()
        if (!clientScopesResponse.isSuccessful) {
            val replacement = "${clientScopesResponse.message()} - (${clientScopesResponse.errorBody()?.source()}"
            throw RemocraResponseException(errorType, replacement)
        }

        val audienceScope = clientScopesResponse.body().orEmpty().firstOrNull { it.name == authnSettings.clientScopeApiAudience }
            ?: throw RemocraResponseException(
                errorType,
                "Client scope '${authnSettings.clientScopeApiAudience}' introuvable dans Keycloak",
            )

        val audienceScopeId = audienceScope.id
            ?: throw RemocraResponseException(
                errorType,
                "Client scope '${authnSettings.clientScopeApiAudience}' sans identifiant technique",
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
