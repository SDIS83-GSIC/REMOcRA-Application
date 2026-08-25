package remocra.couverturehydraulique.graphe

import java.util.UUID

class CheminManager {
    fun initializeChemin(peiId: UUID, maxDistance: Double, voie: UUID?): Chemin.CheminTree {
        val tree = Chemin.CheminTree(start = peiId, distanceMax = maxDistance)
        val rootNode = Chemin.CheminNode(
            sommetId = peiId,
            parent = null,
            voieId = voie,
            distance = 0.0,
            side = null,
            geometry = null,
            voieGeom = null,
        )
        tree.addNode(rootNode)
        Chemin.Exploration.addTree(tree)
        return tree
    }
}
