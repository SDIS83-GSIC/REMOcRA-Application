package remocra.geoserver.response

data class CoucheGeoserver(
    val layer: CoucheDescriptionGeoserver,
)

data class CoucheDescriptionGeoserver(
    val name: String,
)
