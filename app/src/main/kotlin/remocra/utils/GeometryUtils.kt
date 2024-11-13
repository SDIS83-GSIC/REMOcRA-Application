package remocra.utils

import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKBReader
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.io.WKTWriter
import remocra.CoordonneesXYSrid

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
    coupleOrCode.split(":".toRegex())!!.dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()

fun sridFromGeom(coupleOrCode: String): Int =
    coupleOrCode.split("=".toRegex())!!.dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()

fun formatPoint(coordonneesXYSrid: CoordonneesXYSrid): Geometry {
    val geometry = WKTReader().read("POINT(${coordonneesXYSrid.coordonneeX} ${coordonneesXYSrid.coordonneeY})")
        ?: throw IllegalArgumentException("Impossible de convertir les coordonnées en point : $coordonneesXYSrid")

    geometry.srid = coordonneesXYSrid.srid
    return geometry
}
