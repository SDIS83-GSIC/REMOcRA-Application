package remocra.usecase.pei

import com.google.inject.Inject
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.GlobalConstants.SRID_3857
import remocra.GlobalConstants.SRID_4326
import remocra.app.AppSettings
import java.math.BigDecimal
import java.math.RoundingMode

class GetCoordonneesBySrid {
    @Inject
    lateinit var settings: AppSettings

    companion object {
        // Contrat avec le front
        const val SRID_4326_SEXAGECIMAL = -1
    }

    fun execute(
        coordonneeX: String,
        coordonneeY: String,
        srid: Int,
    ): MutableList<CoordonneesBySysteme> {
        // On veut avoir une liste de coordonnées avec SRID (2154, 4326, 4326(mais sexagecimal), 2972, ...)
        val listeCoordonneesBySystem = mutableListOf<CoordonneesBySysteme>()

        // On ajoute dans la liste celui qu'on a déjà
        listeCoordonneesBySystem.add(
            CoordonneesBySysteme(
                coordonneeX,
                coordonneeY,
                srid,
            ),
        )

        var coordonneeX4326: String? = null
        var coordonneeY4326: String? = null

        // Si on est en 4326 sexagecimal, on calcule les coordonnées 4326
        if (srid == SRID_4326_SEXAGECIMAL) {
            coordonneeX4326 = convertDegresSexagesimauxToDecimaux(coordonneeX)
            coordonneeY4326 = convertDegresSexagesimauxToDecimaux(coordonneeY)

            listeCoordonneesBySystem.add(
                CoordonneesBySysteme(coordonneeX4326, coordonneeY4326, SRID_4326),
            )
        }

        // Si on a les coordonnées en 4326
        if (srid == SRID_4326) {
            coordonneeX4326 = coordonneeX
            coordonneeY4326 = coordonneeY
        }

        // Si on n'a pas encore de coordonnées pour le SRID 4326
        if (coordonneeX4326 == null || coordonneeY4326 == null) {
            val coordonnees4326 = GeometryFactory(PrecisionModel(), srid).createPoint(
                Coordinate(
                    coordonneeX.toDouble(),
                    coordonneeY.toDouble(),
                ),
            ).transformProjection(SRID_4326)
            listeCoordonneesBySystem.add(
                coordonnees4326,
            )

            coordonneeX4326 = coordonnees4326.coordonneeX
            coordonneeY4326 = coordonnees4326.coordonneeY
        }

        // Maintenant on transforme les coordonnées 4326 par le SRID fourni dans le reference.conf
        if (srid != settings.srid) {
            listeCoordonneesBySystem.add(
                GeometryFactory(PrecisionModel(), SRID_4326).createPoint(
                    Coordinate(
                        coordonneeX4326.toDouble(),
                        coordonneeY4326.toDouble(),
                    ),
                ).transformProjection(settings.srid),
            )
        }

        if (srid != SRID_3857) {
            listeCoordonneesBySystem.add(
                GeometryFactory(PrecisionModel(), SRID_4326).createPoint(
                    Coordinate(
                        coordonneeX4326.toDouble(),
                        coordonneeY4326.toDouble(),
                    ),
                ).transformProjection(SRID_3857),
            )
        }

        if (srid != SRID_4326_SEXAGECIMAL) {
            // SRID => -1
            // Doit être sous le format DEGRE°MINUTE'SECONDE''
            listeCoordonneesBySystem.add(
                CoordonneesBySysteme(
                    convertDegresDecimauxToSexagesimaux(coordonneeX4326),
                    convertDegresDecimauxToSexagesimaux(coordonneeY4326),
                    SRID_4326_SEXAGECIMAL,
                ),
            )
        }

        return listeCoordonneesBySystem
    }

    /**
     * Permet de mettre au format : DEGRE°MINUTE'SECONDE''
     * La coordonnée passée en entrée doit être sous la projection 4326
     */
    fun convertDegresDecimauxToSexagesimaux(coordonnee: String): String {
        // Nombre de chiffres après la virgule pour les secondes
        val NOMBRE_CHIFFRES_APRES_VIRGULE = 4

        // Doit être sous le format DEGRE°MINUTE'SECONDE''
        val coordonneeBigDecimal = BigDecimal(coordonnee)
        var coordonneeSexagesimal = "${coordonneeBigDecimal.toInt()}°"

        // On extrait les virgules et on calcul les minutes
        val resteMinuteCoordonneeX = coordonneeBigDecimal.remainder(BigDecimal.ONE)
        val minuteCoordonneesX = resteMinuteCoordonneeX.multiply(BigDecimal("60"))
        coordonneeSexagesimal += "${minuteCoordonneesX.toInt()}'"

        // On recommence avec les secondes
        val resteSecondeCoordonnee = minuteCoordonneesX.remainder(BigDecimal.ONE)

        val secondeCoordonnees = resteSecondeCoordonnee.multiply(BigDecimal("60"))
        coordonneeSexagesimal +=
            "${secondeCoordonnees.setScale(NOMBRE_CHIFFRES_APRES_VIRGULE, RoundingMode.HALF_UP)}''"

        return coordonneeSexagesimal
    }

    /**
     * Permet passer du format DEGRE°MINUTE'SECONDE'' au format 4326
     * La coordonnée passée en entrée doit être sous la forme DEGRE°MINUTE'SECONDE''
     */
    fun convertDegresSexagesimauxToDecimaux(coordonnee: String): String {
        // Nombre de chiffres après la virgule pour les secondes
        val NOMBRE_CHIFFRES_APRES_VIRGULE = 14

        // Split les degres et les minutes /secondes
        val parts: List<String> = coordonnee.split("°".toRegex()).dropLastWhile { it.isEmpty() }

        // Split les minutes et les secondes
        val minutesSecondes = parts[1].split("'".toRegex()).dropLastWhile { it.isEmpty() }

        // get minutes et secondes
        var minutes = BigDecimal(minutesSecondes[0])
        var secondes = BigDecimal(minutesSecondes[1])

        val coord = BigDecimal(parts[0])
        minutes = minutes.divide((BigDecimal("60")), NOMBRE_CHIFFRES_APRES_VIRGULE, RoundingMode.HALF_UP)
        secondes = secondes.divide((BigDecimal("3600")), NOMBRE_CHIFFRES_APRES_VIRGULE, RoundingMode.HALF_UP)

        return coord.add(minutes).add(secondes).toString()
    }

    fun Point.transformProjection(projectionTo: Int): CoordonneesBySysteme {
        val sourceCRS = CRS.decode("EPSG:$srid", true)
        val targetCRS = CRS.decode("EPSG:$projectionTo", true)
        val transform = CRS.findMathTransform(sourceCRS, targetCRS)
        val geometryProjectionTo = JTS.transform(this, transform)
            ?: throw IllegalArgumentException("Impossible de convertir la géometrie $this en $projectionTo")

        return CoordonneesBySysteme(
            geometryProjectionTo.coordinate.x.toString(),
            geometryProjectionTo.coordinate.y.toString(),
            projectionTo,
        )
    }

    data class CoordonneesBySysteme(
        val coordonneeX: String,
        val coordonneeY: String,
        val srid: Int,
    )
}
