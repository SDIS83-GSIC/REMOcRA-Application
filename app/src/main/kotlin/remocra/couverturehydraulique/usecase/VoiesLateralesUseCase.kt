package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.VoieLateraleRepository
import remocra.db.jooq.couverturehydraulique.tables.pojos.Reseau
import remocra.db.jooq.couverturehydraulique.tables.pojos.VoieLaterale
import remocra.usecase.AbstractUseCase
import remocra.usecase.couverturehydraulique.GeometrieUseCase
import java.util.UUID
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min

/**
 * Service pour le calcul des voies latérales
 * Équivalent de la fonction voieslaterales
 */
class VoiesLateralesUseCase @Inject constructor(
    private val geometrieUseCase: GeometrieUseCase,
    private val reseauRepository: ReseauRepository,
    private val voieLateraleRepository: VoieLateraleRepository,
) : AbstractUseCase() {
    companion object {
        const val NOMBRE_MINIMUM_VOIES_NON_ACCESSIBLES = 3
    }

    /**
     * Calcul des voies latérales pour un carrefour donné
     */
    fun computeVoiesLaterales(
        depart: UUID,
        matchingPoint: UUID,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ) {
        voieLateraleRepository.emptyTable()

        val voieCourante = reseauRepository.getById(depart, useReseauImporteWithCourant, idReseauImporte) ?: return

        // Calcul de la géométrie courante (petite section pour éviter les formes exotiques)
        val geometrieCourante = computeCurrentGeometrie(voieCourante, matchingPoint)

        // Parcours de toutes les voies voisines
        val voiesVoisines = getVoiesVoisines(matchingPoint, depart, idReseauImporte, useReseauImporteWithCourant)

        for (voieVoisine in voiesVoisines) {
            val geometrieVoisine = computeGeometrieVoisine(voieVoisine, matchingPoint)
            val angle = computeAngle(geometrieCourante, geometrieVoisine)

            voieLateraleRepository.insert(
                voieVoisine = voieVoisine.reseauId,
                angle = angle,
                traversable = voieVoisine.reseauTraversable ?: true,
            )
        }

        // Marquage des voies de gauche et de droite
        voieLateraleRepository.tagVoieGauche()
        voieLateraleRepository.tagVoieDroite()
        tagVoiesNonAccessibles()
    }

    /**
     * Calcule la géométrie courante (5% de la fin de la voie)
     */
    private fun computeCurrentGeometrie(voie: Reseau, matchingPoint: UUID): LineString {
        val geometrie = voie.reseauGeometrie as LineString

        // Inverser si nécessaire selon la direction
        val geometrieOrientee = if (voie.reseauSommetDestination != matchingPoint) {
            geometrieUseCase.reverseLineString(geometrie)
        } else {
            geometrie
        }

        // Prendre les 5% de la fin
        return geometrieUseCase.lineSubstring(geometrieOrientee, 0.95, 1.0)
    }

    /**
     * Calcule la géométrie voisine (5% du début de la voie)
     */
    private fun computeGeometrieVoisine(voie: Reseau, matchingPoint: UUID): LineString {
        val geometrie = voie.reseauGeometrie as LineString

        // Inverser si nécessaire selon la direction
        val geometrieOrientee = if (voie.reseauSommetSource != matchingPoint) {
            geometrieUseCase.reverseLineString(geometrie)
        } else {
            geometrie
        }

        // Prendre les 5% du début
        return geometrieUseCase.lineSubstring(geometrieOrientee, 0.05, 1.0)
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
     * Obtient toutes les voies voisines connectées au point de correspondance
     */
    private fun getVoiesVoisines(
        matchingPoint: UUID,
        depart: UUID,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): List<Reseau> {
        val voiesSortantes = reseauRepository.getTronconsSortants(
            matchingPoint,
            idReseauImporte,
            useReseauImporteWithCourant,
        ).filter { it.reseauPeiTroncon == null && it.reseauId != depart }

        val voiesEntrantes = reseauRepository.getTronconsEntrants(
            matchingPoint,
            idReseauImporte,
            useReseauImporteWithCourant,
        ).filter { it.reseauPeiTroncon == null && it.reseauId != depart }

        return voiesSortantes + voiesEntrantes
    }

    /**
     * Marque les voies non accessibles selon les règles métier
     */
    private fun tagVoiesNonAccessibles() {
        val nombreVoies = voieLateraleRepository.countVoies()
        if (nombreVoies >= NOMBRE_MINIMUM_VOIES_NON_ACCESSIBLES) {
            val voieGauche = voieLateraleRepository.getVoieGauche()
            val voieDroite = voieLateraleRepository.getVoieDroite()

            if (voieGauche?.voieLateraleTraversable == false &&
                voieDroite?.voieLateraleTraversable == false
            ) {
                val angleGauche = voieGauche.voieLateraleAngle
                val angleDroite = voieDroite.voieLateraleAngle

                if (angleGauche != null && angleDroite != null) {
                    voieLateraleRepository.tagVoiesNonAccessibles(angleGauche, angleDroite)
                }
            }
        }
    }

    /**
     * Obtient la voie de gauche
     */
    fun getVoieGauche(): VoieLaterale? {
        return voieLateraleRepository.getVoieGauche()
    }

    /**
     * Obtient la voie de droite
     */
    fun getVoieDroite(): VoieLaterale? {
        return voieLateraleRepository.getVoieDroite()
    }

    /**
     * Obtient une voie latérale spécifique
     */
    fun getVoieLaterale(voieId: UUID): VoieLaterale? {
        return voieLateraleRepository.getByVoieVoisine(voieId)
    }

    /**
     * Obtient la première voie non traversable (pour les calculs de priorité)
     */
    fun getFirstVoieNonTraversable(ordreAngle: String): VoieLaterale? {
        return voieLateraleRepository.getFirstVoieNonTraversable(ordreAngle)
    }

    /**
     * Vérifie si une voie est dans la liste des voies latérales
     */
    fun isVoieLaterale(voieId: UUID): Boolean {
        return voieLateraleRepository.isVoieLaterale(voieId)
    }

    /**
     * Vide la table des voies latérales
     */
    fun emptyTable() {
        voieLateraleRepository.emptyTable()
    }

    /**
     * Vérifie si aucune voie latérale n'existe
     */
    fun aucuneVoieLaterale(): Boolean {
        val voieGauche = getVoieGauche()
        val voieDroite = getVoieDroite()

        return voieGauche == null && voieDroite == null
    }
}
