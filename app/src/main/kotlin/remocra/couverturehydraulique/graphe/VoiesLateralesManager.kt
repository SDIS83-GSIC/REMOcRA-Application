package remocra.couverturehydraulique.graphe

import java.util.*

/**
 * Manager pour la gestion des voies latérales en mémoire (graphe)
 * Plus aucun accès base, tout se fait sur graphe.voiesLaterales
 */
class VoiesLateralesManager {

    companion object {
        private const val DEFAULT_ANGLE = 0.0
    }

    /**
     * Réinitialise la liste des voies latérales du graphe
     */
    fun emptyList(graphe: Graphe) {
        graphe.voiesLaterales.clear()
    }

    /**
     * Ajoute une nouvelle voie latérale au graphe
     */
    fun addVoieLaterale(
        voieVoisine: UUID,
        angle: Double,
        traversable: Boolean,
        graphe: Graphe,
        accessible: Boolean = true,
    ) {
        graphe.voiesLaterales.add(
            VoieLateraleGraphe(
                id = UUID.randomUUID(),
                voieVoisine = voieVoisine,
                angle = angle,
                gauche = false,
                droite = false,
                traversable = traversable,
                accessible = accessible,
            ),
        )
    }

    /**
     * Marque la voie de gauche (angle minimum)
     */
    fun tagVoieGauche(graphe: Graphe) {
        val angleMin = graphe.voiesLaterales.minOfOrNull { it.angle!! }
        if (angleMin != null) {
            graphe.voiesLaterales.filter { it.angle == angleMin }.forEach {
                it.gauche = true
            }
        }
    }

    /**
     * Marque la voie de droite (angle maximum)
     */
    fun tagVoieDroite(graphe: Graphe) {
        val angleMax = graphe.voiesLaterales.maxOfOrNull { it.angle!! }
        if (angleMax != null) {
            graphe.voiesLaterales.filter { it.angle == angleMax }.forEach {
                it.droite = true
            }
        }
    }

    /**
     * Marque les voies non accessibles entre deux angles
     */
    fun tagVoiesNonAccessibles(angleMin: Double, angleMax: Double, graphe: Graphe) {
        graphe.voiesLaterales.filter { it.angle!! > angleMin && it.angle!! < angleMax }.forEach {
            it.accessible = false
        }
    }

    /**
     * Obtient la voie de gauche
     */
    fun getVoieGauche(graphe: Graphe): VoieLateraleGraphe? {
        return graphe.voiesLaterales.find { it.gauche == true }
    }

    /**
     * Obtient la voie de droite
     */
    fun getVoieDroite(graphe: Graphe): VoieLateraleGraphe? {
        return graphe.voiesLaterales.find { it.droite == true }
    }

    /**
     * Obtient une voie latérale par ID de voie voisine
     */
    fun getByVoieVoisine(voieId: UUID, graphe: Graphe): VoieLateraleGraphe? {
        return graphe.voiesLaterales.find { it.voieVoisine == voieId }
    }

    /**
     * Obtient la première voie non traversable par ordre d'angle (ASC ou DESC)
     */
    fun getFirstVoieNonTraversable(ordreAngle: AngleOrdre, graphe: Graphe): VoieLateraleGraphe? {
        val voies = graphe.voiesLaterales.filter { it.traversable == false }
        return when (ordreAngle) {
            AngleOrdre.DESC -> voies.maxByOrNull { it.angle ?: DEFAULT_ANGLE }
            AngleOrdre.ASC -> voies.minByOrNull { it.angle ?: DEFAULT_ANGLE }
        }
    }

    /**
     * Vérifie si une voie est dans la liste des voies latérales
     */
    fun isVoieLaterale(voieId: UUID, graphe: Graphe): Boolean {
        return graphe.voiesLaterales.any { it.voieVoisine == voieId }
    }
}
