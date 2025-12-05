package remocra.usecase.geometrie

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import remocra.GlobalConstants
import remocra.usecase.AbstractUseCase

class CalculGeometrieUseCase : AbstractUseCase() {

    @Inject lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    fun createPointWithSridFromCoordinates(coordonneeX: String?, coordonneeY: String?, srid: Int): Point? {
        if (coordonneeX.isNullOrEmpty() || coordonneeY.isNullOrEmpty()) {
            return null
        }
        var coordinate: Coordinate
        if (srid == -1) {
            coordinate = Coordinate(
                getCoordonneesBySrid.convertDegresSexagesimauxToDecimaux(coordonneeX).toDouble(),
                getCoordonneesBySrid.convertDegresSexagesimauxToDecimaux(coordonneeY).toDouble(),
            )
        } else {
            coordinate = Coordinate(coordonneeX.toDouble(), coordonneeY.toDouble())
        }
        val geometry = GeometryFactory().createPoint(coordinate)
        geometry.srid = if (srid == -1) GlobalConstants.SRID_4326 else srid
        return geometry
    }
}
