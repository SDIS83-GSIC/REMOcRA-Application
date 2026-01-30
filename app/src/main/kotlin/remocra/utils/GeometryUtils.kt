package remocra.utils

import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKBReader
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.io.WKTWriter

fun org.jooq.Geometry.toGeomFromText(srid: String): Field<Geometry?> = DSL.field("ST_GeomFromText('${this.data()}', '${sridFromEpsgCode(srid)}')", Geometry::class.java)

fun Geometry.toGeomFromText(): Field<Geometry?> = DSL.field("ST_GeomFromText('${this.toText()}', '${this.srid}')", Geometry::class.java)

fun Geometry.toJooq(): org.jooq.Geometry = org.jooq.Geometry.valueOf(WKTWriter().write(this))

fun org.jooq.Geometry.toJts(): Geometry = WKBReader().let { reader -> reader.read(WKBReader.hexToBytes(this.data())) }

/**
 * Renvoie un polygon WKT depuis une bbox (xmin,ymin,xmax,ymax)
 */
fun wktFromBBox(bbox: String): String {
    val coord = bbox.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return "POLYGON((${coord[0]} ${coord[1]},${coord[0]} ${coord[3]},${coord[2]} ${coord[3]},${coord[2]} ${coord[1]},${coord[0]} ${coord[1]}))"
}

/**
 * Renvoie une geometry depuis une bbox (xmin,ymin,xmax,ymax) et un EPSG
 */
fun geometryFromBBox(bbox: String?, epsg: String?): Geometry? {
    val polygon = bbox?.let { wktFromBBox(it) } ?: return null
    val srid = epsg?.let { sridFromEpsgCode(it) } ?: return null
    val geometry: Geometry = try {
        WKTReader().read(polygon)
    } catch (e: ParseException) {
        throw RuntimeException("Not a WKT String : $polygon")
    }
    geometry.srid = srid
    return geometry
}
fun sridFromEpsgCode(coupleOrCode: String): Int =
    coupleOrCode.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().last().toInt()

fun sridFromGeom(coupleOrCode: String): Int =
    coupleOrCode.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().last().toInt()

/**
 * Calcule le centroid d'une liste de géométries.
 *
 * Cette méthode prend une liste de géométries, les combine en une collection géométrique
 * et calcule le centroid de l'ensemble. Si la liste est vide, la fonction retourne `null`.
 *
 * @param geometries la liste des géométries dont on souhaite calculer le centroid.
 *                   Chaque géométrie doit partager le même système de coordonnées (SRID).
 * @return le centroid de la collection de géométries, ou `null` si la liste est vide.
 *
 * @throws IllegalArgumentException si les géométries ne partagent pas le même système de coordonnées (SRID).

 */
fun calculerCentroide(geometries: Collection<Geometry>): Point? {
    // Vérification si la liste est vide
    if (geometries.isEmpty()) return null

    // Création d'une collection géométrique si besoin
    val geometryFactory = geometries.first().factory
    val collection = geometryFactory.createGeometryCollection(geometries.toTypedArray())
    val centroide = collection.centroid
    centroide.srid = geometries.first().srid
    // Calcul du centroide à partir de la collection
    return centroide
}

fun transform(input: Geometry, targetCRS: CoordinateReferenceSystem, srid: Int): Geometry {
    val sourceCRS = CRS.decode("EPSG:${input.srid}", true)
    val geom = JTS.transform(input, CRS.findMathTransform(sourceCRS, targetCRS))
        ?: throw IllegalArgumentException("Impossible de convertir la géometrie $input en ${targetCRS.name}")
    geom.srid = srid
    return geom
}
