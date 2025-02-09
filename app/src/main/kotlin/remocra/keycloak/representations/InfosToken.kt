package remocra.keycloak.representations

import com.fasterxml.jackson.annotation.JsonProperty

data class InfosToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
)
