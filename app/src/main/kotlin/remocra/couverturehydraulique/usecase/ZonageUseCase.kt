package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.couverturehydraulique.GeometrieUtils
import remocra.couverturehydraulique.db.CouvertureTraceePeiRepository
import remocra.couverturehydraulique.db.CouvertureTraceeRepository
import remocra.couverturehydraulique.db.ParametreRepository
import remocra.couverturehydraulique.db.PeiRepository.PeiCouvertureHydraulique
import remocra.couverturehydraulique.graphe.Graphe
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Service pour le calcul des zones de couverture hydraulique et des risques
 * Équivalent de la fonction couverture_hydraulique_zonage
 */
class ZonageUseCase @Inject constructor(
    private val geometrieUtils: GeometrieUtils,
    private val couvertureTraceeRepository: CouvertureTraceeRepository,
    private val couvertureTraceePeiRepository: CouvertureTraceePeiRepository,
    private val parametreRepository: ParametreRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {
    companion object {
        // Risque courant faible : 1 PEI non gros débit à distance proche
        const val DISTANCE_RISQUE_COURANT_FAIBLE = 100

        // Risque courant ordinaire : 2 PEI non gros débit, intersection proche/éloignée
        const val DISTANCE_RISQUE_COURANT_ORDINAIRE_PROCHE = 100
        const val DISTANCE_RISQUE_COURANT_ORDINAIRE_ELOIGNEE = 300

        // Risque particulier : intersection PEI gros débit à distance proche/éloignée
        const val DISTANCE_RISQUE_PARTICULIER_PROCHE = 50
        const val DISTANCE_RISQUE_PARTICULIER_ELOIGNEE = 250

        // Distance maximale pour considérer deux PEI comme "proches" (intersection)
        const val DISTANCE_MAX_INTERSECTION_PEIS = 1000

        const val LABEL_RISQUE_COURANT_FAIBLE = "risque_courant_faible"
        const val LABEL_RISQUE_COURANT_ORDINAIRE = "risque_courant_ordinaire"
        const val LABEL_RISQUE_COURANT_IMPORTANT = "risque_courant_important"
        const val LABEL_RISQUE_PARTICULIER = "risque_particulier"
    }

    /**
     * Tracé des zones d'isodistances
     */
    fun traceZoneIsodistance(idEtude: UUID, distance: Int) {
        // Suppression des anciennes données
        couvertureTraceeRepository.deleteByLabelAndEtude("${distance}m", idEtude)

        // Union de toutes les couvertures PEI pour cette distance
        var couvertureDistance: Geometry? = null

        val couverturesPei = couvertureTraceePeiRepository.getByDistanceAndEtude(distance, idEtude)

        for (couverturePei in couverturesPei) {
            couvertureDistance = geometrieUtils.safeUnion(
                couvertureDistance,
                couverturePei.couvertureTraceePeiGeometrie,
            )
        }
        couvertureDistance?.srid = appSettings.srid
        // Insertion de la zone d'isodistance
        couvertureTraceeRepository.insert("${distance}m", idEtude, couvertureDistance)
    }

    /**
     * Calcul des zones de risque selon les règles métier
     */
    fun calculateRiskZones(idEtude: UUID, graphe: Graphe) {
        val codeSdis = appSettings.codeSdis
        if (parametreRepository.existsFonctionSpecifiqueSdis(codeSdis)) {
            parametreRepository.executeFonctionSpecifiqueSdis(codeSdis, idEtude)
        } else {
            calculateDefaultRiskZones(idEtude, graphe)
        }
    }

    /**
     * Default risk zones calculation (distances array for compatibility, not used internally for now)
     */
    private fun calculateDefaultRiskZones(idEtude: UUID, graphe: Graphe) {
        calculateLowRisk(idEtude)
        calculateOrdinaryRisk(idEtude, graphe)
        calculateHighRisk(idEtude)
        calculateSpecialRisk(idEtude)
    }

    /**
     * Tracé du risque courant faible
     * Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
     */
    private fun calculateLowRisk(idEtude: UUID) {
        var couvertureRisqueCourantFaible: Geometry? = null

        val couverturesPei = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(DISTANCE_RISQUE_COURANT_FAIBLE, idEtude, appSettings.codeSdis)

        for (couverturePei in couverturesPei) {
            couvertureRisqueCourantFaible = geometrieUtils.safeUnion(
                couvertureRisqueCourantFaible,
                couverturePei.couvertureTraceePeiGeometrie,
            )
        }

        saveRiskZone(idEtude, LABEL_RISQUE_COURANT_FAIBLE, couvertureRisqueCourantFaible)
    }

    /**
     * Tracé du risque courant ordinaire
     * Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m
     */
    private fun calculateOrdinaryRisk(idEtude: UUID, graphe: Graphe) {
        var couvertureRisqueCourantOrdinaire: Geometry? = null
        val couverturesPeiProche = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(DISTANCE_RISQUE_COURANT_ORDINAIRE_PROCHE, idEtude, appSettings.codeSdis)
        val couverturesPeiEloignee = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(DISTANCE_RISQUE_COURANT_ORDINAIRE_ELOIGNEE, idEtude, appSettings.codeSdis)
        for (couverturePei in couverturesPeiProche) {
            val peiA = toPeiCouvertureHydraulique(couverturePei)
            for (couvertureVoisin in couverturesPeiEloignee) {
                val peiB = toPeiCouvertureHydraulique(couvertureVoisin)
                if (peiA.peiId != peiB.peiId) {
                    val distGraphe = distanceGraphe(peiA, peiB, graphe)
                    if (distGraphe != null && distGraphe <= DISTANCE_MAX_INTERSECTION_PEIS) {
                        val intersection = geometrieUtils.safeIntersection(
                            couverturePei.couvertureTraceePeiGeometrie,
                            couvertureVoisin.couvertureTraceePeiGeometrie,
                        )
                        couvertureRisqueCourantOrdinaire = geometrieUtils.safeUnion(
                            couvertureRisqueCourantOrdinaire,
                            intersection,
                        )
                    }
                }
            }
        }
        saveRiskZone(idEtude, LABEL_RISQUE_COURANT_ORDINAIRE, couvertureRisqueCourantOrdinaire)
    }

    /**
     * Tracé du risque courant important
     * À ce stade, identique au risque courant ordinaire
     */
    private fun calculateHighRisk(idEtude: UUID) {
        val couvertureOrdinaire = couvertureTraceeRepository.getGeometrieByLabelAndEtude(
            LABEL_RISQUE_COURANT_ORDINAIRE,
            idEtude,
        )

        saveRiskZone(idEtude, LABEL_RISQUE_COURANT_IMPORTANT, couvertureOrdinaire)
    }

    /**
     * Tracé du risque particulier
     * Conditions: Intersection distances 50m et 250m, au moins un des deux PEI gros débit
     */
    private fun calculateSpecialRisk(idEtude: UUID) {
        var couvertureRisqueParticulier: Geometry? = null
        // Étape 1: couverture 50m d'un gros débit avec une couverture 250m (tous PEI)
        val couverturesGrosDebitProche = couvertureTraceePeiRepository.getCouverturesGrosDebit(DISTANCE_RISQUE_PARTICULIER_PROCHE, idEtude, appSettings.codeSdis)
        val couverturesEloignee = couvertureTraceePeiRepository.getByDistanceAndEtude(DISTANCE_RISQUE_PARTICULIER_ELOIGNEE, idEtude)
        couvertureRisqueParticulier = unionIntersectedCoverages(
            couvertureRisqueParticulier,
            couverturesGrosDebitProche,
            couverturesEloignee,
        )
        // Étape 2: couverture 250m d'un gros débit avec une couverture 50m (tous PEI)
        val couverturesGrosDebitEloignee = couvertureTraceePeiRepository.getCouverturesGrosDebit(DISTANCE_RISQUE_PARTICULIER_ELOIGNEE, idEtude, appSettings.codeSdis)
        val couverturesProche = couvertureTraceePeiRepository.getByDistanceAndEtude(DISTANCE_RISQUE_PARTICULIER_PROCHE, idEtude)
        couvertureRisqueParticulier = unionIntersectedCoverages(
            couvertureRisqueParticulier,
            couverturesGrosDebitEloignee,
            couverturesProche,
        )
        saveRiskZone(idEtude, LABEL_RISQUE_PARTICULIER, couvertureRisqueParticulier)
    }

    private fun unionIntersectedCoverages(
        initialGeometry: Geometry?,
        couverturesA: List<remocra.db.jooq.couverturehydraulique.tables.pojos.CouvertureTraceePei>,
        couverturesB: List<remocra.db.jooq.couverturehydraulique.tables.pojos.CouvertureTraceePei>,
    ): Geometry? {
        var result = initialGeometry
        for (couvertureA in couverturesA) {
            val geomA = couvertureA.couvertureTraceePeiGeometrie
            for (couvertureB in couverturesB) {
                val geomB = couvertureB.couvertureTraceePeiGeometrie
                if (couvertureA.couvertureTraceePeiId != couvertureB.couvertureTraceePeiId && geomA != null && geomB != null) {
                    val distSpatial = geomA.centroid.distance(geomB.centroid)
                    if (distSpatial <= DISTANCE_MAX_INTERSECTION_PEIS) {
                        val intersection = geometrieUtils.safeIntersection(geomA, geomB)
                        result = geometrieUtils.safeUnion(result, intersection)
                    }
                }
            }
        }
        return result
    }

    /**
     * Calcule la distance réelle sur le graphe entre deux PEI (en mètres)
     */
    private fun distanceGraphe(peiA: PeiCouvertureHydraulique, peiB: PeiCouvertureHydraulique, graphe: Graphe): Double? {
        val sommetA = graphe.sommets.values.find { it.geometrie == peiA.peiGeometrie }
        val sommetB = graphe.sommets.values.find { it.geometrie == peiB.peiGeometrie }
        if (sommetA == null || sommetB == null) return null
        // Utilisation d'un BFS ou Dijkstra simplifié pour trouver la distance réelle
        val visited = mutableSetOf<UUID>()
        val queue = ArrayDeque<Pair<UUID, Double>>()
        queue.add(Pair(sommetA.id, 0.0))
        while (queue.isNotEmpty()) {
            val (current, dist) = queue.removeFirst()
            if (current == sommetB.id) return dist
            if (!visited.add(current)) continue
            val voisins = graphe.sommets[current]?.voisins?.values ?: continue
            for (voie in voisins) {
                val next = if (voie.source == current) voie.destination else voie.source
                if (next != null && !visited.contains(next)) {
                    queue.add(Pair(next, dist + voie.geometrie.length))
                }
            }
        }
        return null // Pas de chemin trouvé
    }

    private fun toPeiCouvertureHydraulique(couverture: remocra.db.jooq.couverturehydraulique.tables.pojos.CouvertureTraceePei): PeiCouvertureHydraulique {
        return PeiCouvertureHydraulique(
            peiId = couverture.couvertureTraceePeiId,
            peiGeometrie = couverture.couvertureTraceePeiGeometrie as org.locationtech.jts.geom.Point,
        )
    }

    private fun saveRiskZone(idEtude: UUID, label: String, geometry: Geometry?) {
        geometry?.srid = appSettings.srid
        // Suppression de l'ancienne zone
        couvertureTraceeRepository.deleteByLabelAndEtude(label, idEtude)
        // Insertion de la nouvelle zone si elle existe
        couvertureTraceeRepository.insert(label, idEtude, geometry)
    }
}
