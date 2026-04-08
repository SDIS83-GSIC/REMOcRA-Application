package remocra.couverturehydraulique.graphe

import org.locationtech.jts.geom.Point
import java.util.UUID

/**
 * Gestion des sommets pour le graphe en mémoire (Kotlin pur, aucune interaction base).
 * Toute logique d'accès ou de modification en base doit être dans SommetRepository côté db.
 */
class SommetManager {
    /**
     * Recherche un sommet par géométrie exacte dans le graphe en mémoire.
     */
    fun findByGeometrie(geometrie: Point, graphe: Graphe): Sommet? {
        return graphe.sommets.values.find { it.geometrie == geometrie }
    }

    /**
     * Retourne la géométrie d'un sommet par son id.
     */
    fun getGeometrie(id: UUID, graphe: Graphe): Point? {
        return graphe.sommets[id]?.geometrie
    }

    /**
     * Crée un sommet en mémoire ou retourne l'existant (exact).
     */
    fun ensureSommet(geometrie: Point, graphe: Graphe): UUID {
        val existant = findByGeometrie(geometrie, graphe)
        return if (existant != null) {
            existant.id
        } else {
            val nouveauId = UUID.randomUUID()
            val nouveauSommet = Sommet(nouveauId, geometrie)
            graphe.sommets[nouveauId] = nouveauSommet
            nouveauId
        }
    }

    /**
     * Crée ou récupère un sommet en mémoire selon une clé spatiale (pour tolérance).
     */
    fun getOrCreateSommet(
        geometrie: Point,
        graphe: Graphe,
        index: MutableMap<Pair<Int, Int>, Sommet>,
        voieId: UUID,
        source: Boolean,
        factor: Double,
    ): UUID {
        val key = Pair((geometrie.x * factor).toInt(), (geometrie.y * factor).toInt())
        val existant = index[key]
        val sommetId = existant?.id ?: run {
            val nouveauId = UUID.randomUUID()
            val nouveauSommet = Sommet(nouveauId, geometrie)
            graphe.sommets[nouveauId] = nouveauSommet
            index[key] = nouveauSommet
            nouveauId
        }
        val voie = graphe.voies[voieId] ?: error("Voie introuvable")
        if (source) voie.source = sommetId else voie.destination = sommetId
        return sommetId
    }
}
