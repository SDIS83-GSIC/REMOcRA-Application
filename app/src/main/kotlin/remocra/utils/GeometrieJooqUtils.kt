package remocra.utils

import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.SelectWhereStep
import org.jooq.TableField
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry

/**
 * Ce fichier répertorie les fonction postgis pour que l'on puisse les appliquer sur les fonctions jOOQ
 */

/**
 * Peut s'appliquer directement sur un from
 */
fun <R : Record?> SelectWhereStep<R>.ST_DWithin(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
): SelectConditionStep<R> =
    where(ST_DWithinCondition(geometrieField, srid, coordonneeX, coordonneeY, distance))

/**
 * S'applique après un where
 */
fun <R : Record?> SelectConditionStep<R>.ST_DWithin(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
): SelectConditionStep<R> =
    and(ST_DWithinCondition(geometrieField, srid, coordonneeX, coordonneeY, distance))

private fun ST_DWithinCondition(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
) =
    "ST_DWithin($geometrieField, 'SRID=$srid;POINT($coordonneeX $coordonneeY)', $distance)"

fun <R : Record?> SelectConditionStep<R>.ST_DistanceInferieurStrict(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
): SelectConditionStep<R> =
    and(ST_DistanceCondition(geometrieField, srid, coordonneeX, coordonneeY, distance, "<"))

fun <R : Record?> SelectWhereStep<R>.ST_DistanceInferieurStrict(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
): SelectConditionStep<R> =
    where(ST_DistanceCondition(geometrieField, srid, coordonneeX, coordonneeY, distance, "<"))

private fun ST_DistanceCondition(
    geometrieField: TableField<Record, org.locationtech.jts.geom.Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
    distance: Int,
    signe: String,
) =
    "ST_Distance($geometrieField, 'SRID=$srid;POINT($coordonneeX $coordonneeY)') $signe $distance"

/**
 * Retourne true si geometrieField est dans la geometrieField2
 */
fun ST_Within(
    geometrieField: Field<Geometry?>,
    geometrieField2: Field<Geometry?>,
): Field<Boolean?> =
    DSL.field("ST_Within($geometrieField, $geometrieField2)", Boolean::class.java)

/**
 * Retourne true si geometrieField est dans la geometrieField2
 */
fun ST_Transform(
    geometrieField: Field<Geometry?>,
    srid: Int?,
): Field<Geometry?> =
    DSL.field("ST_Transform($geometrieField, $srid)", Geometry::class.java)

fun ST_SetSrid(
    geometrieField: Field<Geometry?>,
    srid: Int,
): Field<Geometry?> =
    DSL.field("ST_SetSrid($geometrieField, $srid)", Geometry::class.java)

fun ST_Distance(
    geometrieField: Field<Geometry?>,
    srid: Int,
    coordonneeX: Double,
    coordonneeY: Double,
): Field<Double?> =
    DSL.field("ST_Distance($geometrieField, ST_PointFromText('POINT ($coordonneeX $coordonneeY)', $srid))", Double::class.java)
