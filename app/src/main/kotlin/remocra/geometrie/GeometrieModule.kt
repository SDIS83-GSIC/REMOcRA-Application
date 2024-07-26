package remocra.geometrie

import com.fasterxml.jackson.databind.module.SimpleModule
import org.locationtech.jts.geom.Geometry

class GeometrieModule : SimpleModule() {
    init {
        addSerializer(GeometrieSerializer())
        addDeserializer(Geometry::class.java, GeometrieDeserializer())
    }
}
