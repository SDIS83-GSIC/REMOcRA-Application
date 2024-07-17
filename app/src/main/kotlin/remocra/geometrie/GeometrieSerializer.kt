package remocra.geometrie

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.locationtech.jts.geom.Geometry

class GeometrieSerializer : StdSerializer<Geometry>(Geometry::class.java) {

    override fun serialize(p0: Geometry, p1: JsonGenerator, p2: SerializerProvider) {
        p1.writeString("SRID=${p0.srid};${p0.toText()}")
    }
}
