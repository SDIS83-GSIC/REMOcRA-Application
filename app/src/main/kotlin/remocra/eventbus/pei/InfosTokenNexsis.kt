package remocra.eventbus.pei

import com.fasterxml.jackson.annotation.JsonProperty

data class InfosTokenNexsis(
    @param:JsonProperty("access_token")
    val accessToken: String,
    @param:JsonProperty("token_type")
    val tokenType: String,
)
