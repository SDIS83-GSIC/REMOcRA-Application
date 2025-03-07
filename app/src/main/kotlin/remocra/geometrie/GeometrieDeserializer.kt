package remocra.geometrie

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKTReader

class GeometrieDeserializer : StdDeserializer<Geometry>(Geometry::class.java) {

    private val reader: WKTReader = WKTReader()

    /**
     * La géométrie doit être au format WKT avec SRID en en-tête
     * SRID=XXXX;GEOMETRY(...COORDINATES)
     * GEOMETRY étant POINT, LINESTRING, POLYGON, etc. et COORDINATES un ensemble de coordonnées décimales
     */
    @Throws(ParseException::class, IllegalArgumentException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Geometry {
        return parser.text.split(";").let {
                wkt ->
            val srid = wkt[0].split("=")[1].toInt()
            val geometry: Geometry = reader.read(wkt[1])
            geometry.srid = srid
            geometry
        }
    }
}
