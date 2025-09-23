package remocra.geometrie

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.geojson.GeoJsonWriter

/**
 * Sérialiseur Jackson pour convertir une géométrie JTS en GeoJSON
 * on utilse writeRawValue pour éviter les guillemets autour de l'objet GeoJSON (on veut un objet JSON, pas une chaîne de caractères)
 */
class GeometryToGeoJsonSerializer : JsonSerializer<Geometry>() {
    override fun serialize(value: Geometry, gen: JsonGenerator, serializers: SerializerProvider) {
        val geoJsonWriter = GeoJsonWriter()
        geoJsonWriter.setEncodeCRS(false) // NexSIS ne veut pas du CRS, puisqu'on sait qu'on est en 4326, on le retire
        val geoJson = geoJsonWriter.write(value)
        gen.writeRawValue(geoJson)
    }
}
