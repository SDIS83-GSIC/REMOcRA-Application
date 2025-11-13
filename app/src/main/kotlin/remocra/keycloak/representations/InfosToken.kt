package remocra.keycloak.representations

import com.fasterxml.jackson.annotation.JsonProperty

data class InfosToken(
    @param:JsonProperty("access_token")
    val accessToken: String,
    @param:JsonProperty("token_type")
    val tokenType: String,
)
