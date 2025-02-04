package remocra.geometrie

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.locationtech.jts.geom.Geometry

class GeometrieSerializer : StdSerializer<Geometry>(Geometry::class.java) {

    /**
     * Renvoie une géométrie au format WKT avec SRID : SRID=XXXX;GEOMETRY(...COORDINATES)
     * GEOMETRY étant POINT, LINESTRING, POLYGON, etc. et COORDINATES un ensemble de coordonnées décimales
     */
    override fun serialize(geometry: Geometry, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString("SRID=${geometry.srid};${geometry.toText()}")
    }
}
