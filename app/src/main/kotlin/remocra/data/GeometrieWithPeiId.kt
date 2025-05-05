package remocra.data

import org.locationtech.jts.geom.Point
import java.util.UUID

data class GeometrieWithPeiId(
    val peiGeometrie: Point,
    val peiId: UUID,
)
