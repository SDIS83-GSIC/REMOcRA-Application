package remocra.couverturehydraulique.graphe

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import remocra.app.AppSettings
import remocra.couverturehydraulique.GeometrieUtils
import remocra.db.jooq.couverturehydraulique.enums.TypeSide
import java.util.UUID

class Chemin {

    companion object {
        private const val DEFAULT_RATIO = 1.0
        private const val TOLERANCE = 0.0
    }

    data class CheminNode(
        val sommetId: UUID,
        var distance: Double,
        var parent: CheminNode?,
        val voieId: UUID?,
        val side: TypeSide?,
        var geometry: Geometry?,
        var voieGeom: LineString?,
    )

    data class CheminTree(
        val start: UUID,
        val distanceMax: Double,
    ) {
        // Permet de stocker plusieurs chemins par sommet
        val nodes: MutableMap<UUID, MutableList<CheminNode>> = mutableMapOf()

        fun addNode(node: CheminNode) {
            if (node.parent != null) {
                // Mise à jour du parent pour pointer sur la bonne instance
                val parentList = nodes[node.parent!!.sommetId]
                if (parentList != null && parentList.isNotEmpty()) {
                    node.parent = parentList.first()
                }
            }
            val list = nodes.getOrPut(node.sommetId) { mutableListOf() }
            list.add(node)
        }

        fun deepCopy(): CheminTree {
            val newTree = CheminTree(start, distanceMax)
            val nodeCopies = mutableMapOf<UUID, MutableList<CheminNode>>()
            for ((id, nodeList) in nodes) {
                nodeCopies[id] = nodeList.map { node ->
                    CheminNode(
                        sommetId = node.sommetId,
                        distance = node.distance,
                        parent = null,
                        voieId = node.voieId,
                        side = node.side,
                        geometry = node.geometry,
                        voieGeom = node.voieGeom,
                    )
                }.toMutableList()
            }
            for ((id, nodeList) in nodes) {
                val copyList = nodeCopies[id]
                nodeList.forEachIndexed { idx, node ->
                    copyList?.get(idx)?.parent = node.parent?.sommetId?.let { parentId ->
                        nodeCopies[parentId]?.firstOrNull()
                    }
                }
            }
            newTree.nodes.putAll(nodeCopies)
            return newTree
        }

        fun truncate(
            distance: Int,
            geometrieUtils: GeometrieUtils,
            profondeurCouverture: Int,
            appSettings: AppSettings,
        ): CheminTree {
            val arcsATronquer = nodes.values.flatten().filter {
                val parentDist = it.parent?.distance ?: 0.0
                parentDist < distance && it.distance > distance && it.voieId != null && it.voieGeom != null
            }

            val sousTree = this.deepCopy()
            sousTree.nodes.values.forEach { it.removeIf { node -> node.distance > distance } }

            arcsATronquer.forEach { node ->
                val parentNode = node.parent ?: return@forEach
                val parentDistance = parentNode.distance
                val portion = distance - parentDistance

                val geomVoie = node.voieGeom ?: return@forEach
                val ratio = if (geomVoie.length > 0.0) portion / geomVoie.length else DEFAULT_RATIO
                val clampedRatio = ratio.coerceIn(0.0, DEFAULT_RATIO)
                if (clampedRatio <= TOLERANCE) return@forEach

                val geomTronquee = geometrieUtils.lineSubstring(geomVoie, 0.0, clampedRatio)

                val tailleBuffer = profondeurCouverture.toDouble()
                val side = parentNode.side ?: TypeSide.BOTH
                val (bufferSide, bufferEndCap) = when (side) {
                    TypeSide.LEFT -> BUFFER_SIDE_LEFT to BUFFER_ENDCAP_FLAT
                    TypeSide.RIGHT -> BUFFER_SIDE_RIGHT to BUFFER_ENDCAP_FLAT
                    else -> BUFFER_SIDE_BOTH to BUFFER_ENDCAP_ROUND
                }

                var bufferTronque = geometrieUtils.createBuffer(
                    geomTronquee,
                    tailleBuffer,
                    bufferSide,
                    bufferEndCap,
                )

                if (bufferTronque.geometryType == MULTIPOLYGON_TYPE) {
                    bufferTronque = bufferTronque.getGeometryN(0)
                }
                bufferTronque.srid = appSettings.srid

                val parentSousTree = sousTree.nodes[parentNode.sommetId]
                if (parentSousTree != null) {
                    val nouveauSommetId = UUID.randomUUID()
                    val nouvelleNode = CheminNode(
                        sommetId = nouveauSommetId,
                        distance = distance.toDouble(),
                        parent = parentSousTree.first(),
                        voieId = node.voieId,
                        side = parentNode.side,
                        geometry = bufferTronque,
                        voieGeom = geomTronquee,
                    )
                    sousTree.nodes.computeIfAbsent(nouveauSommetId) { mutableListOf() }.add(nouvelleNode)
                }
            }

            return sousTree
        }
    }

    object Exploration {
        val trees = mutableListOf<CheminTree>()

        fun addTree(tree: CheminTree) {
            trees.add(tree)
        }

        fun purgeOldTrees() {
            trees.removeAll(trees)
        }
    }
}
