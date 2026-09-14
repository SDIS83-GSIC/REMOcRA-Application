package remocra.couverturehydraulique.graphe

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import java.util.UUID

/**
 * Repository pour les données de réseau
 */
class ReseauManager {

    /**
     * Trouve le tronçon le plus proche d'un point
     */
    fun findClosestTroncon(
        point: Point,
        distanceMax: Int,
        graphe: Graphe,
    ): Voie? {
        // Recherche du tronçon le plus proche selon la distance réelle (géométrique)
        return graphe.voies.values
            .asSequence()
            .filter { voie ->
                val dist = voie.geometrie.distance(point)
                val sourceVoisins = graphe.sommets[voie.source]?.voisins?.keys?.toList() ?: emptyList()
                val destVoisins = graphe.sommets[voie.destination]?.voisins?.keys?.toList() ?: emptyList()
                val isConnected = (sourceVoisins.isNotEmpty() || destVoisins.isNotEmpty())
                dist <= distanceMax && isConnected
            }
            .minByOrNull { voie ->
                voie.geometrie.distance(point)
            }
    }

    /**
     * Obtient un tronçon par ID
     */
    fun getById(id: UUID, graphe: Graphe): Voie? {
        return graphe.voies.values.find { it.id == id }
    }

    fun insert(
        geometrie: LineString,
        traversable: Boolean? = null,
        sensUnique: Boolean? = null,
        niveau: Int? = null,
        peiTroncon: UUID? = null,
        sommetSource: UUID? = null,
        sommetDestination: UUID? = null,
        graphe: Graphe,
    ): UUID {
        val id = UUID.randomUUID()
        val voie = Voie(
            id = id,
            geometrie = geometrie,
            source = sommetSource,
            destination = sommetDestination,
            traversable = traversable ?: true,
            sensUnique = sensUnique ?: false,
            niveau = niveau ?: 0,
            peiTroncon = peiTroncon,
            // PAS de champ etudeId/idEtude dans le graphe
        )
        graphe.voies[id] = voie
        // --- Mise à jour des sommets et des voisins ---
        if (sommetSource != null) {
            val sommetSrc = graphe.sommets.getOrPut(sommetSource) { Sommet(sommetSource, graphe.sommets[sommetSource]!!.geometrie) }
            sommetSrc.voisins[id] = voie
        }
        if (sommetDestination != null) {
            val sommetDst = graphe.sommets.getOrPut(sommetDestination) { Sommet(sommetDestination, graphe.sommets[sommetDestination]!!.geometrie) }
            sommetDst.voisins[id] = voie
        }
        // --- Fin mise à jour ---
        return id
    }

    fun updateSommetSource(id: UUID, sommetSource: UUID, graphe: Graphe) {
        graphe.voies[id]?.source = sommetSource
    }
    fun updateSommetDestination(id: UUID, sommetDestination: UUID, graphe: Graphe) {
        graphe.voies[id]?.destination = sommetDestination
    }

    fun delete(id: UUID, graphe: Graphe) {
        graphe.voies.remove(id)
    }

    /**
     * Obtient les tronçons non traversables qui intersectent une géométrie
     */
    fun getTronconsNonTraversablesIntersectant(
        geometrie: Geometry,
        tronconExclu: UUID,
        graphe: Graphe,
    ): List<Voie> {
        return graphe.voies.values.filter { voie ->
            voie.peiTroncon == null &&
                voie.traversable == false &&
                voie.niveau == 0 &&
                voie.id != tronconExclu &&
                geometrie.intersects(voie.geometrie)
        }
    }

    /**
     * Obtient le sommet source d'un tronçon PEI
     */
    fun getSommetSourcePei(peiId: UUID, graphe: Graphe): UUID? {
        return graphe.voies.values.find { it.peiTroncon == peiId }?.source
    }

    /**
     * Obtient l'ID d'un tronçon PEI
     */
    fun getIdTronconPei(peiId: UUID, graphe: Graphe): UUID? {
        return graphe.voies.values.find { it.peiTroncon == peiId }?.id
    }
}
