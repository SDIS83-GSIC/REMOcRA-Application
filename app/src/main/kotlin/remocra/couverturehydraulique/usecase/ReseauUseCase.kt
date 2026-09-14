package remocra.couverturehydraulique.usecase
import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.app.AppSettings
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.db.PeiRepository
import remocra.couverturehydraulique.graphe.Graphe
import remocra.couverturehydraulique.graphe.ReseauManager
import remocra.couverturehydraulique.graphe.SommetManager
import remocra.couverturehydraulique.graphe.Voie
import remocra.usecase.AbstractUseCase
import java.util.UUID
/**
 * UseCase pour la gestion du réseau et des jonctions PEI
 * Équivalent des fonctions inserer_jonction_pei et retirer_jonction_pei
 */
class ReseauUseCase @Inject constructor(
    private val geometrieUtils: GeometrieUtils,
    private val reseauManager: ReseauManager,
    private val sommetManager: SommetManager,
    private val peiRepository: PeiRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {
    companion object {
        private const val FRACTION_MIN = 0.00001
        private const val FRACTION_MAX = 0.99999
        private const val DISTANCE_TOLERANCE = 0.001
        private const val DEFAULT_DISTANCE = 0.0
    }

    // Suivi des tronçons créés et modifiés pour chaque PEI (clé = peiId)
    private val tronconsCreesParPei = mutableMapOf<UUID, MutableList<UUID>>()
    private val tronconsModifiesParPei = mutableMapOf<UUID, MutableList<Voie>>()
    private fun registerTronconForPei(peiId: UUID, tronconId: UUID) {
        tronconsCreesParPei.computeIfAbsent(peiId) { mutableListOf() }.add(tronconId)
    }
    private fun registerTronconModifieForPei(peiId: UUID, voie: Voie) {
        tronconsModifiesParPei.computeIfAbsent(peiId) { mutableListOf() }.add(voie.copy())
    }
    fun restoreGraphePourPei(peiId: UUID, graphe: Graphe) {
        // Supprimer tous les tronçons créés
        tronconsCreesParPei[peiId]?.forEach { id ->
            graphe.voies.remove(id)
        }
        // Supprimer tous les tronçons modifiés (ceux qui ont été découpés/remplacés)
        tronconsModifiesParPei[peiId]?.forEach { voieOriginale ->
            graphe.voies.remove(voieOriginale.id)
        }
        // Réinsérer les originaux
        tronconsModifiesParPei[peiId]?.forEach { voieOriginale ->
            graphe.voies[voieOriginale.id] = voieOriginale
        }
        // Nettoyer les structures de suivi
        tronconsCreesParPei.remove(peiId)
        tronconsModifiesParPei.remove(peiId)
    }

    /**
     * Insère une jonction PEI sur le réseau hydraulique.
     *
     * @param peiId Identifiant du PEI à connecter.
     * @param distanceMaxAuReseau Distance maximale pour la jonction.
     * @return `true` si la jonction a été créée, `false` sinon (pei trop loin du réseau).
     */
    fun insertJonctionPei(
        peiId: UUID,
        distanceMaxAuReseau: Int,
        graphe: Graphe,
    ): Boolean {
        val pei = peiRepository.getById(peiId) ?: return false
        val jonction = reseauManager.findClosestTroncon(
            pei.peiGeometrie,
            distanceMaxAuReseau,
            graphe,
        )?.let { voie ->
            val tronconGeom = voie.geometrie
            val pointProche = computePointPlusProcheSurLigne(tronconGeom, pei.peiGeometrie)
            val fraction = computeFractionOnLine(tronconGeom, pointProche)
            val distance = pei.peiGeometrie.distance(pointProche)
            JonctionInfo(
                tronconId = voie.id,
                jonctionGeometrie = pointProche,
                fraction = fraction,
                peiGeometrie = pei.peiGeometrie,
                tronconGeometrie = tronconGeom,
                distance = distance,
            )
        } ?: return false
        return when {
            jonction.fraction in FRACTION_MIN..FRACTION_MAX -> {
                splitTroncon(jonction, peiId, graphe, sommetManager)
            }
            else -> {
                createJonctionExtremite(jonction, pei, graphe, sommetManager)
            }
        }
    }

    /**
     * Retire la jonction PEI du réseau.
     *
     */
    fun removeJonctionPei(idPei: UUID, graphe: Graphe): Boolean {
        restoreGraphePourPei(idPei, graphe)
        return true
    }

    private data class JonctionInfo(
        val tronconId: UUID,
        val jonctionGeometrie: Point,
        val fraction: Double,
        val peiGeometrie: Point,
        val tronconGeometrie: LineString,
        val distance: Double,
    )

    /**
     * Calcule le point le plus proche sur une LineString (équivalent ST_ClosestPoint)
     */
    private fun computePointPlusProcheSurLigne(ligne: LineString, point: Point): Point {
        var minDistance = Double.MAX_VALUE
        var closestPoint: Coordinate? = null
        val coordinates = ligne.coordinates
        for (i in 0 until coordinates.size - 1) {
            val segmentClosest = geometrieUtils.calculateClosestPointOnSegment(
                coordinates[i],
                coordinates[i + 1],
                point.coordinate,
            )
            val distance = point.coordinate.distance(segmentClosest)
            if (distance < minDistance) {
                minDistance = distance
                closestPoint = segmentClosest
            }
        }
        return GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(closestPoint ?: coordinates[0])
    }

    /**
     * Sépare un tronçon en deux parties lors de l'ajout d'une jonction.
     *
     * @param jonction Informations sur la jonction.
     * @param peiId Identifiant du PEI.
     * @return `true` si l'opération a réussi, `false` sinon.
     */
    private fun splitTroncon(
        jonction: JonctionInfo,
        peiId: UUID,
        graphe: Graphe,
        sommetManager: SommetManager,
    ): Boolean {
        val tronconOriginal = reseauManager.getById(jonction.tronconId, graphe) ?: return false
        // Sauvegarde du tronçon original avant suppression
        registerTronconModifieForPei(peiId, tronconOriginal)
        // SUPPRESSION DU TRONÇON ORIGINAL (équivalent SQL)
        reseauManager.delete(jonction.tronconId, graphe)
        val premierePartie = geometrieUtils.lineSubstring(
            tronconOriginal.geometrie,
            0.0,
            jonction.fraction,
        )
        val deuxiemePartie = geometrieUtils.lineSubstring(
            jonction.tronconGeometrie,
            jonction.fraction,
            1.0,
        )
        val nouvelleVoieId1 = reseauManager.insert(
            geometrie = premierePartie,
            traversable = tronconOriginal.traversable,
            sensUnique = tronconOriginal.sensUnique,
            niveau = tronconOriginal.niveau,
            graphe = graphe,
        )
        registerTronconForPei(peiId, nouvelleVoieId1)
        val nouvelleVoieId2 = reseauManager.insert(
            geometrie = deuxiemePartie,
            traversable = tronconOriginal.traversable,
            sensUnique = tronconOriginal.sensUnique,
            niveau = tronconOriginal.niveau,
            graphe = graphe,
        )
        registerTronconForPei(peiId, nouvelleVoieId2)
        val sommetSourceId = sommetManager.ensureSommet(jonction.peiGeometrie, graphe)
        val sommetDestinationId = sommetManager.ensureSommet(jonction.jonctionGeometrie, graphe)
        val jonctionTronconId = reseauManager.insert(
            geometrie = geometrieUtils.makeLine(jonction.peiGeometrie, jonction.jonctionGeometrie),
            peiTroncon = peiId,
            sommetSource = sommetSourceId,
            sommetDestination = sommetDestinationId,
            graphe = graphe,
        )
        registerTronconForPei(peiId, jonctionTronconId)
        createSommetsAndConnexionsSplit(jonction, tronconOriginal, nouvelleVoieId1, nouvelleVoieId2, jonctionTronconId, graphe, sommetManager)
        return true
    }

    /**
     * Crée une jonction à une extrémité du tronçon.
     *
     * @param jonction Informations sur la jonction.
     * @param pei Objet PEI à connecter.
     * @return `true` si la jonction a été créée, `false` sinon.
     */
    private fun createJonctionExtremite(
        jonction: JonctionInfo,
        pei: PeiRepository.PeiCouvertureHydraulique,
        graphe: Graphe,
        sommetManager: SommetManager,
    ): Boolean {
        val tronconOriginal = reseauManager.getById(jonction.tronconId, graphe)
        if (tronconOriginal != null) {
            registerTronconModifieForPei(pei.peiId, tronconOriginal)
        }
        val extremiteSummit = when {
            tronconOriginal == null -> null
            jonction.fraction <= FRACTION_MIN -> tronconOriginal.source
            jonction.fraction >= 1.0 - FRACTION_MIN -> tronconOriginal.destination
            else -> null
        }
        val sommetJonctionId = extremiteSummit
            ?: sommetManager.ensureSommet(jonction.jonctionGeometrie, graphe)
        val sommetPeiId = sommetManager.ensureSommet(pei.peiGeometrie, graphe)
        val tronconJonctionId = reseauManager.insert(
            geometrie = geometrieUtils.makeLine(
                pei.peiGeometrie,
                jonction.jonctionGeometrie,
            ),
            peiTroncon = pei.peiId,
            sommetSource = sommetPeiId,
            sommetDestination = sommetJonctionId,
            graphe = graphe,
        )
        registerTronconForPei(pei.peiId, tronconJonctionId)
        return true
    }

    /**
     * Calcule la fraction de la position d'un point sur une ligne.
     *
     * @param ligne Ligne sur laquelle calculer.
     * @param point Point de référence.
     * @return Fraction (entre 0 et 1) représentant la position sur la ligne.
     */
    private fun computeFractionOnLine(ligne: LineString, point: Point): Double {
        val longueurTotale = ligne.length
        var distanceParcourue = DEFAULT_DISTANCE
        val coordinates = ligne.coordinates
        for (i in 0 until coordinates.size - 1) {
            val segmentClosest = geometrieUtils.calculateClosestPointOnSegment(
                coordinates[i],
                coordinates[i + 1],
                point.coordinate,
            )
            if (point.coordinate.distance(segmentClosest) < DISTANCE_TOLERANCE) {
                val distanceSegment = coordinates[i].distance(segmentClosest)
                return (distanceParcourue + distanceSegment) / longueurTotale
            }
            distanceParcourue += coordinates[i].distance(coordinates[i + 1])
        }
        return DEFAULT_DISTANCE
    }

    /**
     * Crée les sommets et met à jour les connexions pour deux tronçons découpés et la jonction PEI.
     */
    private fun updateVoisins(voieId: UUID, graphe: Graphe) {
        val voie = graphe.voies[voieId] ?: return
        voie.source?.let { sommetId ->
            val sommet = graphe.sommets[sommetId]
            sommet?.voisins?.put(voieId, voie)
        }
        voie.destination?.let { sommetId ->
            val sommet = graphe.sommets[sommetId]
            sommet?.voisins?.put(voieId, voie)
        }
    }
    private fun createSommetsAndConnexionsSplit(
        jonction: JonctionInfo,
        tronconOriginal: Voie,
        voieId1: UUID,
        voieId2: UUID,
        jonctionTronconId: UUID,
        graphe: Graphe,
        sommetManager: SommetManager,
    ) {
        val sommetJonctionId = sommetManager.ensureSommet(jonction.jonctionGeometrie, graphe)
        val sommetPeiId = sommetManager.ensureSommet(jonction.peiGeometrie, graphe)
        reseauManager.updateSommetDestination(voieId1, sommetJonctionId, graphe)
        tronconOriginal.source?.let {
            reseauManager.updateSommetSource(voieId1, it, graphe)
        }
        reseauManager.updateSommetSource(voieId2, sommetJonctionId, graphe)
        tronconOriginal.destination?.let {
            reseauManager.updateSommetDestination(voieId2, it, graphe)
        }
        reseauManager.updateSommetSource(jonctionTronconId, sommetPeiId, graphe)
        reseauManager.updateSommetDestination(jonctionTronconId, sommetJonctionId, graphe)
        // Mise à jour explicite des voisins pour garantir la continuité du graphe
        updateVoisins(voieId1, graphe)
        updateVoisins(voieId2, graphe)
        updateVoisins(jonctionTronconId, graphe)
    }
}
