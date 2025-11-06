package remocra.utils

import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry

/**
 * Ce fichier répertorie les fonctions postgis pour que l'on puisse les appliquer sur les fonctions jOOQ
 */

/**
 * Renvoie un champ de type Geometry représentant un Point
 * @param x Float
 * @param y Float
 * @return Champ de type Geometry
 */
fun ST_MakePoint(
    x: Float,
    y: Float,
): Field<Geometry?> =
    DSL.field("ST_MakePoint({0}, {1})", Geometry::class.java, DSL.inline(x), DSL.inline(y))

/**
 * Renvoie la distance en Float entre 2 champs géométrie
 * @param geometryField champ de type Geometry
 * @param geometryField2 champ de type Geometry
 * @return Champ de type Float
 */
fun ST_Distance(
    geometryField: Field<Geometry?>,
    geometryField2: Field<Geometry?>,
): Field<Float?> =
    DSL.field("ST_Distance({0}, {1})", Float::class.java, geometryField, geometryField2)

/**
 * Retourne true si le champ geometryField est contenu dans la distance distanceOfSrid du champ geometryField2
 * @param geometryField champ de type Geometry
 * @param geometryField2 champ de type Geometry
 * @param distanceOfSrid distance en Double
 * @return Champ de type Boolean
 */
fun ST_DWithin(
    geometryField: Field<Geometry?>,
    geometryField2: Field<Geometry?>,
    distanceOfSrid: Double,
): Field<Boolean?> =
    DSL.field("ST_DWithin({0}, {1}, {2})", Boolean::class.java, geometryField, geometryField2, distanceOfSrid)

/**
 * Retourne true si le champ geometryField est contenu dans le champ geometryField2
 * @param geometryField champ de type Geometry
 * @param geometryField2 champ de type Geometry
 * @return Champ de type Boolean
 */
fun ST_Within(
    geometryField: Field<Geometry?>,
    geometryField2: Field<Geometry?>,
): Field<Boolean?> =
    DSL.field("ST_Within({0}, {1})", Boolean::class.java, geometryField, geometryField2)

/**
 * Retourne true si geometryField et geometryField2 s'intersectent, se croisent
 * @param geometryField champ de type Geometry
 * @param geometryField2 champ de type Geometry
 * @return Champ de type Boolean
 */
fun ST_Intersects(
    geometryField: Field<Geometry?>,
    geometryField2: Field<Geometry?>,
): Field<Boolean?> =
    DSL.field("ST_Intersects({0}, {1})", Boolean::class.java, geometryField, geometryField2)

/**
 * Transforme une géométrie en fonction d'un SRID
 * @param geometryField champ de type Geometry
 * @param srid SRID cible
 * @return Champ Geometry converti
 */
fun ST_Transform(
    geometryField: Field<Geometry?>,
    srid: Int?,
): Field<Geometry?> =
    DSL.field("ST_Transform({0}, {1})", Geometry::class.java, geometryField, srid)

/**
 * Applique un SRID sur un champ Geometry, ne transforme pas les coordonnées
 * @param geometryField champ de type Geometry
 * @param srid nouveau SRID à appliquer
 * @return Champ Geometry avec nouveau SRID
 */
fun ST_SetSrid(
    geometryField: Field<Geometry?>,
    srid: Int,
): Field<Geometry?> =
    DSL.field("ST_SetSrid({0}, {1})", Geometry::class.java, geometryField, srid)

/**
 * Renvoie une géométrie qui est l'union d'un tableau de géométries.
 * @param geometryList Geometry?: le tableau de géométries dont l'union est à faire
 * @return Champ de type Geometry
 */
fun ST_Union(geometry: Field<Geometry?>): Field<Geometry?> =
    DSL.field("ST_Union({0})", Geometry::class.java, geometry)

/**
 * Renvoie une géométrie multi (MultiPoint, MultiLineString, MultiPolygon) à partir d'une géométrie donnée.
 * Garantie que le résultat est une collection de géométries du même type.
 * @param geometry Geometry? : la géométrie à convertir en une géométrie multi
 * @return Champ de type Geometry
 */
fun ST_Multi(geometry: Field<Geometry?>): Field<Geometry?> =
    DSL.field("ST_Multi({0})", Geometry::class.java, geometry)
