package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.graphe.AngleOrdre
import remocra.couverturehydraulique.graphe.Graphe
import remocra.couverturehydraulique.graphe.GrapheManager
import remocra.couverturehydraulique.graphe.ReseauManager
import remocra.couverturehydraulique.graphe.Voie
import remocra.couverturehydraulique.graphe.VoieLateraleGraphe
import remocra.couverturehydraulique.graphe.VoiesLateralesManager
import remocra.usecase.AbstractUseCase
import java.util.UUID
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min

class VoiesLateralesUseCase @Inject constructor(
    private val geometrieUtils: GeometrieUtils,
    private val reseauManager: ReseauManager,
    private val grapheManager: GrapheManager,
    private val voiesLateralesManager: VoiesLateralesManager,
) : AbstractUseCase() {

    /**
     * Calcul des voies latérales pour un carrefour donné
     */
    fun computeVoiesLaterales(
        depart: UUID,
        matchingPoint: UUID,
        graphe: Graphe,
    ) {
        val voieCourante = reseauManager.getById(depart, graphe) ?: return
        val geometrieCourante = computeGeometrieCourante(voieCourante, matchingPoint)
        var voiesVoisines = graphe.sommets[matchingPoint]?.voisins
        if (voiesVoisines == null) {
            grapheManager.getVoisinsSommets(matchingPoint, graphe)
            voiesVoisines = graphe.sommets[matchingPoint]?.voisins
        }
        if (voiesVoisines != null) {
            for (voieVoisine in voiesVoisines.values) {
                val geometrieVoisine = computeGeometrieVoisine(voieVoisine, matchingPoint)
                val angle = computeAngle(geometrieCourante, geometrieVoisine)
                voiesLateralesManager.addVoieLaterale(
                    voieVoisine.id,
                    angle,
                    voieVoisine.traversable,
                    graphe,
                )
            }
        }
        // Marquage des voies de gauche et de droite
        voiesLateralesManager.tagVoieGauche(graphe)
        voiesLateralesManager.tagVoieDroite(graphe)
        tagVoiesNonAccessibles(graphe)
    }

    /**
     * Calcule la géométrie courante (5% de la fin de la voie)
     */
    private fun computeGeometrieCourante(voie: Voie, matchingPoint: UUID): LineString {
        val geometrie = voie.geometrie
        val geometrieOrientee = if (voie.destination != matchingPoint) {
            geometrieUtils.reverseLineString(geometrie)
        } else {
            geometrie
        }
        return geometrieUtils.lineSubstring(geometrieOrientee, 0.95, 1.0)
    }

    /**
     * Calcule la géométrie voisine (5% du début de la voie)
     */
    private fun computeGeometrieVoisine(voie: Voie, matchingPoint: UUID): LineString {
        val geometrie = voie.geometrie
        val geometrieOrientee = if (voie.source != matchingPoint) {
            geometrieUtils.reverseLineString(geometrie)
        } else {
            geometrie
        }
        return geometrieUtils.lineSubstring(geometrieOrientee, 0.0, 0.05)
    }

    /**
     * Calcule l'angle entre deux géométries linéaires
     */
    private fun computeAngle(geometrieCourante: LineString, geometrieVoisine: LineString): Double {
        val startCourante = geometrieCourante.startPoint
        val endCourante = geometrieCourante.endPoint
        val startVoisine = geometrieVoisine.startPoint

        return computeAngleBetweenPoints(startCourante, endCourante, startVoisine)
    }

    /**
     * Calcule l'angle entre trois points
     */
    private fun computeAngleBetweenPoints(start: Point, end: Point, target: Point): Double {
        val dx1 = end.x - start.x
        val dy1 = end.y - start.y
        val dx2 = target.x - start.x
        val dy2 = target.y - start.y

        val angle1 = atan2(dy1, dx1)
        val angle2 = atan2(dy2, dx2)

        var diff = angle2 - angle1

        // Normaliser l'angle entre 0 et 2π

        diff = min(diff, 2 * PI - diff)

        return diff
    }

    /**
     * Marque les voies non accessibles selon les règles métier
     */
    private fun tagVoiesNonAccessibles(graphe: Graphe) {
        val nombreVoies = graphe.voiesLaterales.size
        if (nombreVoies >= 3) {
            val voieGauche = voiesLateralesManager.getVoieGauche(graphe)
            val voieDroite = voiesLateralesManager.getVoieDroite(graphe)
            if (voieGauche?.traversable == false &&
                voieDroite?.traversable == false
            ) {
                val angleGauche = voieGauche.angle
                val angleDroite = voieDroite.angle
                if (angleGauche != null && angleDroite != null) {
                    voiesLateralesManager.tagVoiesNonAccessibles(angleGauche, angleDroite, graphe)
                }
            }
        }
    }

    /**
     * Obtient la voie de gauche
     */
    fun getVoieGauche(graphe: Graphe): VoieLateraleGraphe? {
        return voiesLateralesManager.getVoieGauche(graphe)
    }

    /**
     * Obtient la voie de droite
     */
    fun getVoieDroite(graphe: Graphe): VoieLateraleGraphe? {
        return voiesLateralesManager.getVoieDroite(graphe)
    }

    /**
     * Obtient une voie latérale spécifique
     */
    fun getVoieLaterale(voieId: UUID, graphe: Graphe): VoieLateraleGraphe? {
        return voiesLateralesManager.getByVoieVoisine(voieId, graphe)
    }

    /**
     * Obtient la première voie non traversable (pour les calculs de priorité)
     */
    fun getFirstVoieNonTraversable(ordreAngle: AngleOrdre, graphe: Graphe): VoieLateraleGraphe? {
        return voiesLateralesManager.getFirstVoieNonTraversable(ordreAngle, graphe)
    }

    /**
     * Vérifie si une voie est dans la liste des voies latérales
     */
    fun isVoieLaterale(voieId: UUID, graphe: Graphe): Boolean {
        return voiesLateralesManager.isVoieLaterale(voieId, graphe)
    }

    /**
     * Vérifie si aucune voie latérale n'existe
     */
    fun hasNoVoieLaterale(graphe: Graphe): Boolean {
        val voieGauche = getVoieGauche(graphe)
        val voieDroite = getVoieDroite(graphe)

        return voieGauche == null && voieDroite == null
    }
}
