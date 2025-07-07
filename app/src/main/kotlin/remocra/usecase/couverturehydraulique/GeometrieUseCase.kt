package remocra.usecase.couverturehydraulique

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.operation.buffer.BufferOp
import org.locationtech.jts.operation.buffer.BufferParameters
import remocra.app.AppSettings
import remocra.usecase.AbstractUseCase

/**
 * Service pour les opérations géométriques équivalentes aux fonctions PostGIS
 */
class GeometrieUseCase @Inject constructor(
    private val appSettings: AppSettings,
) : AbstractUseCase() {

    /**
     * Équivalent de safe_union
     */
    fun safeUnion(geomA: Geometry?, geomB: Geometry?): Geometry? {
        return try {
            when {
                geomA != null && geomB != null -> geomA.union(geomB)
                else -> geomA ?: geomB
            }
        } catch (_: Exception) {
            try {
                val bufferedA = geomA?.buffer(0.0000001)
                val bufferedB = geomB?.buffer(0.0000001)
                bufferedA?.union(bufferedB)
            } catch (_: Exception) {
                geomA
            }
        }
    }

    /**
     * Équivalent de safe_isect
     */
    fun safeIntersection(geomA: Geometry?, geomB: Geometry?): Geometry? {
        return try {
            if (geomA == null || geomB == null) {
                return null
            }
            geomA.intersection(geomB)
        } catch (_: Exception) {
            try {
                val bufferedA = geomA!!.buffer(0.0000001)
                val bufferedB = geomB!!.buffer(0.0000001)
                bufferedA.intersection(bufferedB)
            } catch (_: Exception) {
                GeometryFactory(PrecisionModel(), appSettings.srid).createPolygon()
            }
        }
    }

    /**
     * Création d'un buffer avec options (équivalent ST_BUFFER avec paramètres)
     */
    fun createBuffer(
        geometry: Geometry,
        distance: Double,
        side: String = "both",
        endCap: String = "round",
    ): Geometry {
        val bufferOp = BufferOp(geometry)

        // Configuration du type de terminaison
        val capStyle = when (endCap.lowercase()) {
            "flat" -> BufferParameters.CAP_FLAT
            "square" -> BufferParameters.CAP_SQUARE
            else -> BufferParameters.CAP_ROUND
        }

        val params = BufferParameters().apply { endCapStyle = capStyle }

        return when (side) {
            "left" -> createSingleSideBuffer(geometry, distance, true, params)
            "right" -> createSingleSideBuffer(geometry, distance, false, params)
            else -> {
                val res = bufferOp.getResultGeometry(distance)
                res.srid = appSettings.srid
                res
            }
        }
    }

    /**
     * Création d'un buffer d'un seul côté (simulation)
     */
    private fun createSingleSideBuffer(
        geometry: Geometry,
        distance: Double,
        isLeft: Boolean,
        params: BufferParameters,
    ): Geometry {
        val fullBuffer = BufferOp(geometry, params).getResultGeometry(distance)
        val oppositeBuffer = BufferOp(geometry, params).getResultGeometry(-distance * 0.8)
        val singleSide = fullBuffer.difference(oppositeBuffer)

        if (singleSide.numGeometries <= 1) return singleSide

        val sorted = (0 until singleSide.numGeometries)
            .map { singleSide.getGeometryN(it) }
            .sortedBy { it.centroid.x }

        return if (isLeft) sorted.first() else sorted.last()
    }

    /**
     * Équivalent de ST_LineSubstring
     */
    fun lineSubstring(line: LineString, startFraction: Double, endFraction: Double): LineString {
        val coordinates = line.coordinates
        val totalLength = line.length

        // Validation des paramètres
        if (startFraction < 0 || endFraction > 1 || startFraction >= endFraction) {
            return GeometryFactory(PrecisionModel(), appSettings.srid).createLineString()
        }

        val startDistance = totalLength * startFraction
        val endDistance = totalLength * endFraction

        // Calcul des points de début et fin
        val newCoordinates = mutableListOf<Coordinate>()

        var currentDistance = 0.0
        var hasStartPoint = false

        for (i in 0 until coordinates.size - 1) {
            val segmentLength = coordinates[i].distance(coordinates[i + 1])
            val segmentStart = currentDistance
            val segmentEnd = currentDistance + segmentLength

            // Si le segment intersecte avec la zone d'intérêt [startDistance, endDistance]
            if (segmentEnd >= startDistance && segmentStart <= endDistance) {
                // Ajouter le point de début si nécessaire
                if (!hasStartPoint) {
                    if (startDistance in segmentStart..segmentEnd) {
                        // Point de début interpolé dans ce segment
                        val ratio = (startDistance - segmentStart) / segmentLength
                        val interpolated = interpolateCoordinate(coordinates[i], coordinates[i + 1], ratio)
                        newCoordinates.add(interpolated)
                        hasStartPoint = true
                    } else if (segmentStart >= startDistance) {
                        // Le segment commence après startDistance
                        newCoordinates.add(coordinates[i])
                        hasStartPoint = true
                    }
                }

                // Ajouter les points intermédiaires et le point de fin
                if (segmentEnd <= endDistance) {
                    // Tout le segment est dans la zone
                    if (hasStartPoint && coordinates[i + 1] != newCoordinates.lastOrNull()) {
                        newCoordinates.add(coordinates[i + 1])
                    }
                } else {
                    // Le segment dépasse endDistance, interpoler le point de fin
                    val ratio = (endDistance - segmentStart) / segmentLength
                    val interpolated = interpolateCoordinate(coordinates[i], coordinates[i + 1], ratio)
                    if (interpolated != newCoordinates.lastOrNull()) {
                        newCoordinates.add(interpolated)
                    }
                    break
                }
            }

            currentDistance += segmentLength
        }

        // Vérifier qu'on a au moins 2 points pour créer une LineString valide
        return if (newCoordinates.size >= 2) {
            GeometryFactory(PrecisionModel(), appSettings.srid).createLineString(newCoordinates.toTypedArray())
        } else {
            // Retourner une LineString vide si on n'a pas assez de points
            GeometryFactory(PrecisionModel(), appSettings.srid).createLineString()
        }
    }

    /**
     * Interpolation entre deux coordonnées
     */
    private fun interpolateCoordinate(start: Coordinate, end: Coordinate, ratio: Double): Coordinate {
        val x = start.x + (end.x - start.x) * ratio
        val y = start.y + (end.y - start.y) * ratio
        return Coordinate(x, y)
    }

    /**
     * Équivalent de ST_Split - version corrigée pour mieux correspondre au comportement PostGIS
     */
    fun split(geometry: Geometry, blade: Geometry): Geometry {
        return try {
            if (!blade.intersects(geometry)) {
                // Pas d'intersection, retourner la géométrie originale dans une collection
                val factory = GeometryFactory(PrecisionModel(), appSettings.srid)
                return factory.createGeometryCollection(arrayOf(geometry))
            }

            // Buffer minimal sur le blade pour assurer un découpage propre
            val bufferedBlade = blade.buffer(0.0001)
            val difference = geometry.difference(bufferedBlade)

            // ST_Split retourne toujours une GeometryCollection
            if (difference.isEmpty) {
                // Si la différence est vide, retourner la géométrie originale dans une collection
                val factory = GeometryFactory(PrecisionModel(), appSettings.srid)
                factory.createGeometryCollection(arrayOf(geometry))
            } else if (difference is GeometryCollection || difference.numGeometries > 1) {
                // Déjà une collection ou plusieurs géométries
                difference
            } else {
                // Une seule géométrie résultante, la mettre dans une collection
                val factory = GeometryFactory(PrecisionModel(), appSettings.srid)
                factory.createGeometryCollection(arrayOf(difference))
            }
        } catch (_: Exception) {
            // En cas d'erreur, retourner la géométrie originale dans une collection
            val factory = GeometryFactory(PrecisionModel(), appSettings.srid)
            factory.createGeometryCollection(arrayOf(geometry))
        }
    }

    /**
     * Création d'une ligne entre deux points
     */
    fun makeLine(point1: Point, point2: Point): LineString {
        return GeometryFactory(PrecisionModel(), appSettings.srid).createLineString(arrayOf(point1.coordinate, point2.coordinate))
    }

    /**
     * Inversion d'une LineString (équivalent ST_REVERSE)
     */
    fun reverseLineString(line: LineString): LineString {
        val coordinates = line.coordinates
        return GeometryFactory(PrecisionModel(), appSettings.srid).createLineString(coordinates.reversedArray())
    }

    /**
     * Calcul du point le plus proche sur un segment
     */
    fun calculateClosestPointOnSegment(start: Coordinate, end: Coordinate, point: Coordinate): Coordinate {
        val dx = end.x - start.x
        val dy = end.y - start.y

        if (dx == 0.0 && dy == 0.0) {
            return start
        }

        val t = ((point.x - start.x) * dx + (point.y - start.y) * dy) / (dx * dx + dy * dy)

        return when {
            t < 0 -> start
            t > 1 -> end
            else -> Coordinate(start.x + t * dx, start.y + t * dy)
        }
    }

    /**
     * Équivalent de ST_NumGeometries
     */
    fun getNumGeometries(geometry: Geometry): Int {
        return geometry.numGeometries
    }

    /**
     * Équivalent de ST_LineInterpolatePoint
     */
    fun lineInterpolatePoint(lineString: LineString, fraction: Double): Point {
        val totalLength = lineString.length
        val targetDistance = totalLength * fraction.coerceIn(0.0, 1.0)

        var currentDistance = 0.0
        val coordinates = lineString.coordinates

        for (i in 0 until coordinates.size - 1) {
            val segmentLength = coordinates[i].distance(coordinates[i + 1])

            if (currentDistance + segmentLength >= targetDistance) {
                // Le point cible est dans ce segment
                val segmentFraction = (targetDistance - currentDistance) / segmentLength
                val interpolated = interpolateCoordinate(coordinates[i], coordinates[i + 1], segmentFraction)
                return GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(interpolated)
            }

            currentDistance += segmentLength
        }

        // Si on arrive ici, retourner le dernier point
        return GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(coordinates.last())
    }

    /**
     * Trouve la géométrie la plus proche d'un point de référence dans une collection
     */
    fun getClosestGeometry(geometryCollection: Geometry, referencePoint: Point): Geometry? {
        var closestGeometry: Geometry? = null
        var minDistance = Double.MAX_VALUE

        for (i in 0 until geometryCollection.numGeometries) {
            val geom = geometryCollection.getGeometryN(i)
            val distance = geom.distance(referencePoint)

            if (distance < minDistance) {
                minDistance = distance
                closestGeometry = geom
            }
        }

        return closestGeometry
    }

    fun normalizePoint(point: Point, srid: Int = appSettings.srid): Point {
        val coordinateCopy = Coordinate(point.coordinate)
        return GeometryFactory(PrecisionModel(), srid).createPoint(coordinateCopy)
    }

    fun distanceBetween(geometryA: Geometry, geometryB: Geometry): Double {
        return geometryA.distance(geometryB)
    }
}
