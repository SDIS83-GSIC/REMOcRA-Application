package remocra.geometrie

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader

class GeometrieDeserializer : StdDeserializer<Geometry>(Geometry::class.java) {

    private val reader: WKTReader = WKTReader()

    /**
     * La géométrie doit être sous la forme
     * SRID=XXXX;POINT(XXXXXXX XXXXXXX)
     */
    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): Geometry? {
        return p0?.text?.split(";")?.let {
            val srid = it[0].split("=")[1].toInt()
            val geometry: Geometry? = reader.read(it[1])
            return geometry.apply {
                this?.srid = srid
            }
        }
    }
}
