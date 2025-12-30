package remocra.geoserver.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

data class LayerGroupResponse(
    @get:JsonProperty("layerGroup")
    val layerGroup: LayerGroup,
)

data class LayerGroup(
    val name: String?,
    val publishables: Publishables?,
)

data class Publishables(
    @get:JsonFormat(with = [JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY])
    val published: List<Published>?,
)

data class Published(
    val name: String?,
)
