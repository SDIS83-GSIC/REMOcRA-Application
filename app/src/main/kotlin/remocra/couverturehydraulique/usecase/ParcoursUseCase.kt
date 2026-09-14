package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.db.CouvertureTraceePeiRepository
import remocra.couverturehydraulique.db.PeiRepository.PeiCouvertureHydraulique
import remocra.couverturehydraulique.graphe.AngleOrdre
import remocra.couverturehydraulique.graphe.BUFFER_ENDCAP_FLAT
import remocra.couverturehydraulique.graphe.BUFFER_ENDCAP_ROUND
import remocra.couverturehydraulique.graphe.BUFFER_SIDE_BOTH
import remocra.couverturehydraulique.graphe.BUFFER_SIDE_LEFT
import remocra.couverturehydraulique.graphe.BUFFER_SIDE_RIGHT
import remocra.couverturehydraulique.graphe.Chemin
import remocra.couverturehydraulique.graphe.CheminManager
import remocra.couverturehydraulique.graphe.Graphe
import remocra.couverturehydraulique.graphe.GrapheManager
import remocra.couverturehydraulique.graphe.MULTIPOLYGON_TYPE
import remocra.couverturehydraulique.graphe.ReseauManager
import remocra.couverturehydraulique.graphe.SommetManager
import remocra.couverturehydraulique.graphe.Voie
import remocra.couverturehydraulique.graphe.VoieLateraleGraphe
import remocra.db.jooq.couverturehydraulique.enums.TypeSide
import remocra.usecase.AbstractUseCase
import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.UUID
import kotlin.collections.get

/**
 * Service pour le parcours du réseau et calcul des couvertures hydrauliques
 * Équivalent de la fonction parcours_couverture_hydraulique
 */
class ParcoursUseCase @Inject constructor(
    private val sommetManager: SommetManager,
    private val reseauManager: ReseauManager,
    private val geometrieUtils: GeometrieUtils,
    private val appSettings: AppSettings,
    private val voiesLateralesUseCase: VoiesLateralesUseCase,
    private val couvertureTraceePeiRepository: CouvertureTraceePeiRepository,
    private val cheminManager: CheminManager,
    private val grapheManager: GrapheManager,
) : AbstractUseCase() {

    companion object {
        const val BUFFER_SIZE_RESTREINT = 5
        const val POLYGON_TYPE = "Polygon"
        const val BUFFER_DELTA_POS = 0.001
        const val BUFFER_DELTA_NEG = -0.001
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Exécution du parcours principal
     */
    fun executeParcours(
        listePeis: List<PeiCouvertureHydraulique>,
        idEtude: UUID,
        distance: Int,
        profondeurCouverture: Int,
        graphe: Graphe,
    ): Int {
        try {
            for (pei in listePeis) {
                try {
                    executeParcoursPei(
                        pei,
                        idEtude,
                        distance,
                        profondeurCouverture,
                        listePeis,
                        graphe,
                    )
                } catch (e: Exception) {
                    logger.warn("ERREUR lors du parcours du pei ${pei.peiId}: ${e.message}")
                }
            }

            return 1
        } catch (e: Exception) {
            logger.warn("ERREUR dans executeParcours: ${e.message}")
            return 0
        }
    }

    /**
     * Parcours pour une distance donnée
     */
    private fun executeParcoursPei(
        pei: PeiCouvertureHydraulique,
        idEtude: UUID,
        distance: Int,
        profondeurCouverture: Int,
        listePeis: List<PeiCouvertureHydraulique>,
        graphe: Graphe,
    ) {
        val noeudsAVisiter = PriorityQueue<Pair<UUID, Double>>(compareBy { it.second })
        val noeudsVisites = mutableSetOf<UUID>()
        var debutChemin = true
        val index = listePeis.indexOf(pei)
        var tree = Chemin.Exploration.trees.getOrNull(index)
        val depart = pei.peiId

        // Initialisation du parcours
        if (tree == null) {
            if (listePeis[0] == pei) {
                // Premier parcours depuis ce PEI
                val premierNoeud = reseauManager.getSommetSourcePei(depart, graphe)
                if (premierNoeud != null) {
                    // Vérifier la connectivité du sommet dans le graphe
                    tree = cheminManager.initializeChemin(depart, distance.toDouble(), null)
                    if (index >= 0 && index < Chemin.Exploration.trees.size) {
                        Chemin.Exploration.trees[index] = tree
                    } else {
                        Chemin.Exploration.trees.add(tree)
                    }
                    noeudsAVisiter.add(Pair(premierNoeud, 0.0))
                } else {
                    return // Pas de nœud de départ, on ne peut pas continuer
                }
            } else {
                // Avant de commencer un nouveau parcours, on regarde si notre point de départ est inclu dans un arbre déjà parcouru
                var arbre: Int = index
                var distanceMin: Double = distance.toDouble()
                Chemin.Exploration.trees.forEach {
                    val nodeList = it.nodes[pei.peiId]
                    nodeList?.forEach { node ->
                        if (node.distance < distanceMin) {
                            distanceMin = node.distance
                            arbre = Chemin.Exploration.trees.indexOf(it)
                        }
                    }
                }
                val premierNoeud = reseauManager.getSommetSourcePei(depart, graphe)
                if (premierNoeud != null) {
                    // Vérifier la connectivité du sommet dans le graphe
                    tree = cheminManager.initializeChemin(depart, distance.toDouble(), null)
                    if (index >= 0 && index < Chemin.Exploration.trees.size) {
                        Chemin.Exploration.trees[index] = tree
                    } else {
                        Chemin.Exploration.trees.add(tree)
                    }
                    noeudsAVisiter.add(Pair(premierNoeud, 0.0))
                } else {
                    return // Pas de nœud de départ, on ne peut pas continuer
                }
                // Si à ce point, on a arbre différent d'index alors le nœud est contenu dans un arbre donc on recalcule.
                if (arbre != index) {
                    recalculateArbrePartiel(
                        noeudsAVisiter,
                        Chemin.Exploration.trees[index],
                        index,
                        Chemin.Exploration.trees[arbre],
                    )
                }
                // Sinon, on commence un parcours de zéro
            }
        } else {
            // Si l'arbre n'est pas null, on vérifie si on a les mêmes paramètres
            if (tree.start != depart || tree.distanceMax != distance.toDouble()) {
                val premierNoeud = reseauManager.getSommetSourcePei(depart, graphe)
                if (premierNoeud != null) {
                    tree = cheminManager.initializeChemin(depart, distance.toDouble(), null)
                    if (index >= 0 && index < Chemin.Exploration.trees.size) {
                        Chemin.Exploration.trees[index] = tree
                    } else {
                        Chemin.Exploration.trees.add(tree)
                    }
                    noeudsAVisiter.add(Pair(premierNoeud, 0.0))
                } else {
                    return // Pas de nœud de départ, on ne peut pas continuer
                }
            } else {
                return
            }
        }

        // Parcours des nœuds
        while (noeudsAVisiter.isNotEmpty()) {
            val noeudCourant = noeudsAVisiter.poll().first // <-- FIFO (file)
            noeudsVisites.add(noeudCourant)

            try {
                val courantRecordList = tree.nodes[noeudCourant]
                val courantRecord = courantRecordList?.minByOrNull { it.distance }

                var voieCourante: UUID? = null

                if (courantRecord == null) {
                    voieCourante = reseauManager.getIdTronconPei(depart, graphe)
                }
                exploreVoisins(
                    noeudCourant, voieCourante, courantRecord,
                    distance, profondeurCouverture,
                    noeudsAVisiter, noeudsVisites, debutChemin, tree, graphe, index,
                )
            } catch (e: Exception) {
                logger.warn(
                    "Anomalie ignorée sur un noeud du parcours (peiId={}, noeudCourant={}) : {}",
                    depart,
                    noeudCourant,
                    e.message,
                )
                // Continue avec le nœud suivant au lieu d'arrêter tout le parcours
            }

            debutChemin = false // <-- File déjà initialisée
        }

        // Sauvegarde de la couverture calculée
        saveCouverture(depart, idEtude, distance)
    }

    /**
     * Parcours des voisins d'un nœud
     */
    private fun exploreVoisins(
        noeudCourant: UUID,
        voieCourante: UUID?,
        courantRecord: Chemin.CheminNode?,
        distance: Int,
        profondeurCouverture: Int,
        noeudsAVisiter: PriorityQueue<Pair<UUID, Double>>,
        noeudsVisites: MutableSet<UUID>,
        debutChemin: Boolean,
        tree: Chemin.CheminTree,
        graphe: Graphe,
        index: Int,
    ) {
        val voieCalcul = courantRecord?.voieId ?: voieCourante
        if (voieCalcul == null) {
            return
        }

        voiesLateralesUseCase.computeVoiesLaterales(
            voieCalcul,
            noeudCourant,
            graphe,
        )

        val voieGauche = voiesLateralesUseCase.getVoieGauche(graphe)
        val voieDroite = voiesLateralesUseCase.getVoieDroite(graphe)

        // Mapper exactement comme le SQL avec inversion de géométrie pour les entrants
        val voisins = grapheManager.getVoiesNormalisees(graphe.sommets[noeudCourant])

        // Filtrer selon voies latérales APRÈS le mapping
        val voisinsFiltres = voisins.filter { voisin ->
            val estVoieLaterale = voiesLateralesUseCase.isVoieLaterale(voisin.id, graphe)
            val aucuneVoieLaterale = voiesLateralesUseCase.hasNoVoieLaterale(graphe)
            estVoieLaterale || aucuneVoieLaterale
        }

        // Variable locale pour debutChemin qui sera modifiée dans la boucle
        var debutCheminLocal = debutChemin

        for (voisin in voisinsFiltres) {
            if (shouldIgnoreVoisin(voisin, courantRecord?.voieId, debutCheminLocal)) {
                continue
            }
            val voisinLateral = voiesLateralesUseCase.getVoieLaterale(voisin.id, graphe)
            if (voisinLateral != null && voisinLateral.accessible == false) {
                continue
            }
            if (voieGauche != null && voieDroite != null && voieGauche.voieVoisine != voieDroite.voieVoisine) {
                val premiereVoieNonTraversableDroite = voiesLateralesUseCase.getFirstVoieNonTraversable(AngleOrdre.DESC, graphe)
                if (voisinLateral?.gauche == true &&
                    graphe.voies[courantRecord?.voieId]?.traversable == false &&
                    courantRecord?.side == TypeSide.RIGHT &&
                    premiereVoieNonTraversableDroite?.voieVoisine != voisinLateral.voieVoisine
                ) {
                    continue
                }
                val premiereVoieNonTraversableGauche = voiesLateralesUseCase.getFirstVoieNonTraversable(AngleOrdre.ASC, graphe)
                if (voisinLateral?.droite == true &&
                    graphe.voies[courantRecord?.voieId]?.traversable == false &&
                    courantRecord?.side == TypeSide.LEFT &&
                    premiereVoieNonTraversableGauche?.voieVoisine != voisinLateral.voieVoisine
                ) {
                    continue
                }
            }
            val distanceParcourue = computeDistanceParcourue(voisin, courantRecord)
            var voisinAjuste = voisin
            var distanceFinale = distanceParcourue
            var bufferEndPoint: Point? = null
            if (distanceParcourue > distance && (courantRecord?.distance ?: 0.0) < distance) {
                val fraction = when {
                    distanceParcourue <= distance -> 1.0
                    else -> (1.0 - ((distanceParcourue - distance) / voisin.geometrie.length))
                }
                val geometrieTronquee = geometrieUtils.lineSubstring(voisin.geometrie, 0.0, fraction)
                voisinAjuste = voisin.copy(
                    geometrie = geometrieTronquee,
                )
                bufferEndPoint = geometrieTronquee.endPoint
                distanceFinale = distance.toDouble()
            } else {
                if (!debutCheminLocal && voisin.destination != null) {
                    bufferEndPoint = sommetManager.getGeometrie(voisin.destination!!, graphe)
                }
            }

            val nodeList = tree.nodes[voisin.destination]
            var updated = false
            if (nodeList != null) {
                for (node in nodeList) {
                    if (node.voieId == voisin.id) {
                        if (distanceFinale < node.distance) {
                            // Mise à jour de la distance et du parent
                            node.distance = distanceFinale
                            node.parent = courantRecord
                            node.geometry = null // sera mis à jour par sauvegarderChemin
                            node.voieGeom = voisin.geometrie
                            updated = true
                        }
                        // Si la distance n'est pas meilleure, on ne fait rien
                    }
                }
            }
            if (!updated && distanceFinale <= distance) {
                val buffer = createBuffer(
                    voisinAjuste, // Utiliser voisinAjuste au lieu de voisin
                    voieGauche,
                    voieDroite,
                    courantRecord,
                    profondeurCouverture,
                    bufferEndPoint,
                    graphe,
                )
                saveChemin(
                    voisinAjuste,
                    distanceFinale,
                    courantRecord,
                    buffer,
                    courantRecord?.side ?: TypeSide.BOTH,
                    tree,
                    index,
                )
            }
            if (!noeudsVisites.contains(voisin.destination) && distanceFinale < distance && voisin.destination != null) {
                noeudsAVisiter.add(Pair(voisin.destination!!, distanceFinale))
            }
            debutCheminLocal = false
        }
    }

    /**
     * Création du buffer pour une voie
     */
    private fun createBuffer(
        voisin: Voie,
        voieGauche: VoieLateraleGraphe?,
        voieDroite: VoieLateraleGraphe?,
        courantRecord: Chemin.CheminNode?,
        profondeurCouverture: Int,
        bufferEndPoint: Point? = null,
        graphe: Graphe,
    ): Geometry {
        // Détermination du côté du buffer
        val (bufferSide, bufferEndCap) = determineParametresBuffer(
            voisin,
            voieGauche,
            voieDroite,
            courantRecord,
            graphe,
        )

        // Taille du buffer selon le niveau
        val tailleBuffer =
            if (voisin.niveau != 0) BUFFER_SIZE_RESTREINT else profondeurCouverture

        // Création du buffer initial
        var buffer = geometrieUtils.createBuffer(
            voisin.geometrie,
            tailleBuffer.toDouble(),
            bufferSide,
            bufferEndCap,
        )

        // Découpage par les voies non traversables
        buffer = splitByVoiesNonTraversables(
            buffer,
            voisin,
            graphe,
        ).getGeometryN(0) as Polygon

        // Ajout du buffer des sommets si nécessaire et si bufferEndPoint est fourni
        if (bufferSide != BUFFER_SIDE_BOTH && bufferEndPoint != null) {
            buffer = addBufferSommets(buffer, bufferEndPoint, profondeurCouverture, graphe)
        }

        buffer.srid = appSettings.srid
        return buffer
    }

    private fun determineParametresBuffer(
        voisin: Voie,
        voieGauche: VoieLateraleGraphe?,
        voieDroite: VoieLateraleGraphe?,
        courantRecord: Chemin.CheminNode?,
        graphe: Graphe,
    ): Pair<String, String> {
        val bufferSide: String
        val bufferEndCap: String
        when {
            graphe.voies[courantRecord?.voieId]?.traversable == false && !voisin.traversable -> {
                bufferSide = courantRecord?.side?.name ?: BUFFER_SIDE_BOTH
                bufferEndCap = BUFFER_ENDCAP_ROUND
            }
            voisin.traversable -> {
                bufferSide = BUFFER_SIDE_BOTH
                bufferEndCap = BUFFER_ENDCAP_ROUND
            }
            voieGauche?.voieVoisine == voieDroite?.voieVoisine -> {
                bufferSide = courantRecord?.side?.name ?: BUFFER_SIDE_BOTH
                bufferEndCap = BUFFER_ENDCAP_ROUND
            }
            voisin.id == voieGauche?.voieVoisine -> {
                bufferSide = if (courantRecord?.side?.name == BUFFER_SIDE_BOTH) {
                    BUFFER_SIDE_LEFT
                } else courantRecord?.side?.name ?: BUFFER_SIDE_LEFT
                bufferEndCap = BUFFER_ENDCAP_FLAT
            }
            voisin.id == voieDroite?.voieVoisine -> {
                bufferSide = if (courantRecord?.side?.name == BUFFER_SIDE_BOTH) {
                    BUFFER_SIDE_RIGHT
                } else courantRecord?.side?.name ?: BUFFER_SIDE_RIGHT
                bufferEndCap = BUFFER_ENDCAP_FLAT
            }
            else -> {
                bufferSide = BUFFER_SIDE_BOTH
                bufferEndCap = BUFFER_ENDCAP_ROUND
            }
        }
        return Pair(bufferSide, bufferEndCap)
    }

    fun saveCouverture(depart: UUID, idEtude: UUID, distance: Int, tree: Chemin.CheminTree? = null) {
        // Suppression de l'ancienne couverture
        couvertureTraceePeiRepository.delete(distance, depart, idEtude)
        // Utiliser l'arbre fourni si présent, sinon l'arbre global
        val geometries = (tree ?: Chemin.Exploration.trees.find { it.start == depart })
            ?.nodes
            ?.values
            ?.flatten()
            ?.filter { it.distance <= distance }
            ?.mapNotNull { it.geometry }
            ?: emptyList()
        var geometrieUnion: Geometry? = null
        for (geom in geometries) {
            geometrieUnion = geometrieUtils.safeUnion(geometrieUnion, geom)
        }
        // Traitement des MultiPolygon vers Polygon (équivalent SQL v2)
        if (geometrieUnion != null && geometrieUnion.geometryType == MULTIPOLYGON_TYPE) {
            try {
                val buffered = geometrieUtils.createBuffer(geometrieUnion, BUFFER_DELTA_POS)
                geometrieUnion = geometrieUtils.createBuffer(buffered, BUFFER_DELTA_NEG)
            } catch (e: Exception) {
                logger.warn("WARN: Échec de conversion MultiPolygon vers Polygon: " + e.message)
            }
        }
        if (geometrieUnion != null && (geometrieUnion.geometryType == POLYGON_TYPE || geometrieUnion.geometryType == MULTIPOLYGON_TYPE)) {
            geometrieUnion.srid = appSettings.srid
            couvertureTraceePeiRepository.insert(distance, depart, idEtude, geometrieUnion)
        }
    }

    private fun shouldIgnoreVoisin(voisin: Voie, voieCourante: UUID?, debutChemin: Boolean): Boolean {
        return (voisin.peiTroncon != null && !debutChemin) ||
            (voieCourante != null && voisin.id == voieCourante)
    }

    private fun computeDistanceParcourue(voisin: Voie, node: Chemin.CheminNode?): Double {
        return voisin.geometrie.length + (node?.distance ?: 0.0)
    }

    private fun splitByVoiesNonTraversables(
        buffer: Geometry,
        voisin: Voie,
        graphe: Graphe,
    ): Geometry {
        val voiesNonTraversables = reseauManager.getTronconsNonTraversablesIntersectant(
            buffer,
            voisin.id,
            graphe,
        )

        if (voiesNonTraversables.isEmpty()) {
            return buffer
        }

        // Union de toutes les voies non traversables qui intersectent le buffer
        val bladeGeometries = voiesNonTraversables.map { it.geometrie }
        var blade: Geometry? = null

        for (voieGeom in bladeGeometries) {
            blade = if (blade == null) {
                voieGeom
            } else {
                geometrieUtils.safeUnion(blade, voieGeom) ?: blade
            }
        }

        if (blade == null) {
            return buffer
        }

        // Découpage du buffer par les voies non traversables (équivalent ST_SPLIT du SQL)
        val splitResult = geometrieUtils.split(buffer, blade)
        return if (geometrieUtils.getNumGeometries(splitResult) > 1) {
            val pointRef = geometrieUtils.lineInterpolatePoint(voisin.geometrie, BUFFER_DELTA_POS)
            geometrieUtils.getClosestGeometry(splitResult, pointRef) ?: buffer
        } else {
            splitResult
        }
    }

    private fun addBufferSommets(
        buffer: Geometry,
        endPoint: Point,
        profondeurCouverture: Int,
        graphe: Graphe,
    ): Geometry {
        val bufferSommets = geometrieUtils.createBuffer(endPoint, profondeurCouverture.toDouble())

        // Récupération des voies non traversables qui intersectent le buffer du sommet
        val voiesNonTraversables = reseauManager.getTronconsNonTraversablesIntersectant(
            bufferSommets,
            UUID.randomUUID(),
            graphe,
        )

        if (voiesNonTraversables.isEmpty()) {
            return geometrieUtils.safeUnion(buffer, bufferSommets) ?: buffer
        }

        // Union de toutes les voies non traversables
        val bladeGeometries = voiesNonTraversables.map { it.geometrie }
        var bladeSommets: Geometry? = null

        for (voieGeom in bladeGeometries) {
            bladeSommets = if (bladeSommets == null) {
                voieGeom
            } else {
                geometrieUtils.safeUnion(bladeSommets, voieGeom) ?: bladeSommets
            }
        }

        if (bladeSommets == null) {
            return geometrieUtils.safeUnion(buffer, bufferSommets) ?: buffer
        }

        // Découpage du buffer des sommets
        val splitResult = geometrieUtils.split(bufferSommets, bladeSommets)
        var bestGeom: Geometry? = null
        var maxIntersectionRatio = 0.0
        for (i in 0 until geometrieUtils.getNumGeometries(splitResult)) {
            val geom = splitResult.getGeometryN(i)
            val intersection = geometrieUtils.safeIntersection(geom, buffer)
            if (intersection != null && geom.area > 0) {
                val ratio = intersection.area / geom.area
                if (ratio > maxIntersectionRatio) {
                    maxIntersectionRatio = ratio
                    bestGeom = geom
                }
            }
        }
        return if (bestGeom != null) {
            geometrieUtils.safeUnion(buffer, bestGeom) ?: buffer
        } else {
            buffer
        }
    }

    private fun saveChemin(
        voisin: Voie,
        distanceParcourue: Double,
        courantRecord: Chemin.CheminNode?,
        buffer: Geometry,
        side: TypeSide,
        tree: Chemin.CheminTree,
        index: Int,
    ) {
        var bufferFinal = buffer
        if (buffer.geometryType == MULTIPOLYGON_TYPE) {
            bufferFinal = buffer.getGeometryN(0)
        }
        val node = Chemin.CheminNode(
            sommetId = voisin.destination!!,
            distance = distanceParcourue,
            parent = courantRecord,
            voieId = voisin.id,
            side = side,
            geometry = bufferFinal,
            voieGeom = voisin.geometrie,
        )
        tree.addNode(node)
        if (index >= 0 && index < Chemin.Exploration.trees.size) {
            Chemin.Exploration.trees[index] = tree
        } else {
            Chemin.Exploration.trees.add(tree)
        }
    }

    private fun recalculateArbrePartiel(
        noeudsAVisiter: PriorityQueue<Pair<UUID, Double>>,
        tree: Chemin.CheminTree,
        index: Int,
        oldTree: Chemin.CheminTree,
    ) {
        val s2 = tree.start
        val distFromS2 = mutableMapOf<UUID, Double>()
        distFromS2[s2] = 0.0

        val stack = ArrayDeque<Chemin.CheminNode>()
        val startNodeList = oldTree.nodes[s2] ?: return
        startNodeList.forEach { startNode ->
            stack.add(startNode)
            noeudsAVisiter.add(Pair(s2, 0.0))
        }
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()

            val children = tree.nodes.values.flatten().filter { it.parent?.sommetId == node.sommetId }

            for (child in children) {
                val newDist = distFromS2[node.sommetId]!! + (child.distance - node.distance)

                if (newDist > tree.distanceMax) continue

                val oldChildList = oldTree.nodes[child.sommetId]
                val oldChild = oldChildList?.minByOrNull { it.distance }
                val newNode = Chemin.CheminNode(
                    sommetId = child.sommetId,
                    distance = newDist,
                    parent = tree.nodes[node.sommetId]?.minByOrNull { it.distance },
                    voieId = child.voieId,
                    side = child.side,
                    geometry = child.geometry,
                    voieGeom = oldChild?.voieGeom,
                )

                tree.addNode(newNode)
                distFromS2[child.sommetId] = newDist

                stack.add(child)
                noeudsAVisiter.add(Pair(child.sommetId, newDist))
            }
        }
        Chemin.Exploration.trees[index] = tree
    }
}
