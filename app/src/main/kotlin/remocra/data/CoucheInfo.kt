package remocra.data

import com.fasterxml.jackson.databind.JsonNode

data class CoucheInfo(
    val nomCouche: String,
    val paramsCouche: JsonNode,
)
