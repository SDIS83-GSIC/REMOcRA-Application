package remocra.couverturehydraulique.graphe

import jakarta.inject.Inject
import remocra.app.AppSettings
/**
 * Crée la topologie du graphe en mémoire (mise à jour des sommets source/destination pour chaque voie).
 * La tolérance spatiale est gérée par un facteur (par défaut 0.2).
 */
class CreateTopologie @Inject constructor(
    private val sommetManager: SommetManager,
    private val appSettings: AppSettings,
) {

    fun createTopologie(graphe: Graphe, tolerance: Double = 0.2) {
        val factor = 1 / tolerance
        val indexSommets = mutableMapOf<Pair<Int, Int>, Sommet>()
        // Indexation des sommets existants
        graphe.sommets.values.forEach { sommet ->
            val key = Pair((sommet.geometrie.x * factor).toInt(), (sommet.geometrie.y * factor).toInt())
            indexSommets[key] = sommet
        }
        // Mise à jour des voies
        graphe.voies.forEach { (voieId, voie) ->
            val start = voie.geometrie.startPoint.also { it.srid = appSettings.srid }
            val end = voie.geometrie.endPoint.also { it.srid = appSettings.srid }
            sommetManager.getOrCreateSommet(start, graphe, indexSommets, voieId, true, factor)
            sommetManager.getOrCreateSommet(end, graphe, indexSommets, voieId, false, factor)
        }
    }
}
