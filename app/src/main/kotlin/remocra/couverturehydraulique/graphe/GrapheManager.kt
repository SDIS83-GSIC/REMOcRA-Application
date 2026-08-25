package remocra.couverturehydraulique.graphe

import jakarta.inject.Inject
import org.locationtech.jts.geom.Point
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.SommetRepository
import java.util.UUID

class GrapheManager @Inject constructor(
    private val reseauRepository: ReseauRepository,
    private val sommetRepository: SommetRepository,
    private val geometrieUtils: GeometrieUtils,
) {

    /**
     * Charge le graphe en mémoire à partir des données de la base.
     */
    fun loadGraphe(etudeId: UUID?, useReseauImporteWithCourant: Boolean): Graphe {
        val sommetsList: List<remocra.db.jooq.couverturehydraulique.tables.pojos.Sommet> =
            sommetRepository.getSommetsEtude(etudeId, useReseauImporteWithCourant)
        val sommetsMap: MutableMap<UUID, Sommet> = sommetsList.map {
            Sommet(
                id = it.sommetId,
                geometrie = it.sommetGeometrie as Point,
            )
        }.associateBy { it.id }.toMutableMap()

        val voiesList: List<Voie> =
            reseauRepository.getReseauEtude(etudeId, useReseauImporteWithCourant)

        // Correction : ajout des sommets manquants pour chaque voie (source/destination)
        voiesList.forEach { voie ->
            voie.source?.let { src ->
                if (!sommetsMap.containsKey(src)) {
                    val pt = voie.geometrie.startPoint
                    sommetsMap[src] = Sommet(src, pt)
                }
            }
            voie.destination?.let { dst ->
                if (!sommetsMap.containsKey(dst)) {
                    val pt = voie.geometrie.endPoint
                    sommetsMap[dst] = Sommet(dst, pt)
                }
            }
        }

        // Remplissage des voisins sous forme de Map
        voiesList.forEach { voie ->
            voie.source?.let { src ->
                voie.destination?.let { dst ->
                    sommetsMap[src]?.voisins?.put(dst, voie)
                    if (!voie.sensUnique) {
                        sommetsMap[dst]?.voisins?.put(src, voie)
                    }
                }
            }
        }
        val voiesMap = voiesList.associateBy { it.id }.toMutableMap()

        return Graphe(sommetsMap, voiesMap)
    }

    /**
     * Retourne la liste des voisins d'un sommet sous forme d'objets Sommet.
     */
    fun getVoisinsSommets(sommetId: UUID, graphe: Graphe): List<Sommet> {
        return graphe.sommets[sommetId]?.voisins?.keys?.mapNotNull { voisinId ->
            graphe.sommets[voisinId]
        } ?: emptyList()
    }

    /**
     * Retourne les voies normalisées pour un sommet (sens correct, géométrie inversée si besoin).
     */
    fun getVoiesNormalisees(sommet: Sommet?): List<Voie> {
        if (sommet == null || sommet.voisins.isEmpty()) {
            return emptyList()
        }
        return sommet.voisins.values.map { voie ->
            if (voie.destination == sommet.id) {
                // Inverser source/destination
                voie.copy(
                    source = sommet.id,
                    destination = voie.source,
                    geometrie = geometrieUtils.reverseLineString(voie.geometrie),
                )
            } else {
                voie
            }
        }
    }
}
