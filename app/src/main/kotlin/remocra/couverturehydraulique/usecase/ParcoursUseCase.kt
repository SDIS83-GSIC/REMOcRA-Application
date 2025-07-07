package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.slf4j.LoggerFactory
import remocra.app.AppSettings
import remocra.couverturehydraulique.db.CouvertureTraceePeiRepository
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.SommetRepository
import remocra.couverturehydraulique.db.TempDistanceRepository
import remocra.db.jooq.couverturehydraulique.enums.TypeSide
import remocra.db.jooq.couverturehydraulique.tables.pojos.TempDistance
import remocra.db.jooq.couverturehydraulique.tables.pojos.VoieLaterale
import remocra.usecase.AbstractUseCase
import remocra.usecase.couverturehydraulique.GeometrieUseCase
import java.util.UUID

/**
 * Service pour le parcours du réseau et calcul des couvertures hydrauliques
 * Équivalent de la fonction parcours_couverture_hydraulique
 */
class ParcoursUseCase @Inject constructor(
    private val sommetRepository: SommetRepository,
    private val reseauRepository: ReseauRepository,
    private val geometrieUseCase: GeometrieUseCase,
    private val appSettings: AppSettings,
    private val voiesLateralesUseCase: VoiesLateralesUseCase,
    private val tempDistanceRepository: TempDistanceRepository,
    private val couvertureTraceePeiRepository: CouvertureTraceePeiRepository,
) : AbstractUseCase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val BUFFER_SIZE_RESTREINT = 5 // Buffer pour les voies restreintes (niveau != 0)
        const val BUFFER_SIDE_BOTH = "both"
        const val BUFFER_DELTA_POS = 0.001
        const val BUFFER_DELTA_NEG = -0.001
    }

    /**
     * Exécution du parcours principal
     */
    fun executeParcours(
        depart: UUID,
        idEtude: UUID,
        idReseauImporte: UUID?,
        tabDistances: IntArray,
        profondeurCouverture: Int,
        useReseauImporteWithCourant: Boolean,
    ): Int {
        try {
            tempDistanceRepository.emptyTable()

            for (dist in tabDistances) {
                try {
                    executeParcoursDistance(
                        depart,
                        idEtude,
                        idReseauImporte,
                        dist,
                        profondeurCouverture,
                        useReseauImporteWithCourant,
                        tabDistances,
                    )
                } catch (e: Exception) {
                    logger.error("ERREUR lors du parcours: ${e.message}")
                }
            }

            emptyTablesTemporaires()
            return 1
        } catch (e: Exception) {
            logger.error("ERREUR: ${e.message}")
            return 0
        }
    }

    /**
     * Parcours pour une distance donnée
     */
    private fun executeParcoursDistance(
        depart: UUID,
        idEtude: UUID,
        idReseauImporte: UUID?,
        distance: Int,
        profondeurCouverture: Int,
        useReseauImporteWithCourant: Boolean,
        tabDistances: IntArray,
    ) {
        try {
            val noeudsAVisiter = mutableListOf<UUID>()
            val noeudsVisites = mutableListOf<UUID>()
            var debutChemin = true

            // Initialisation du parcours
            if (tabDistances[0] == distance) {
                // Premier parcours depuis ce PEI
                tempDistanceRepository.deleteByPeiDepart(depart)
                val premierNoeud = reseauRepository.getSommetSourcePei(depart)
                if (premierNoeud != null) {
                    noeudsAVisiter.add(premierNoeud)
                } else {
                    return // Pas de nœud de départ, on ne peut pas continuer
                }
            } else {
                // Parcours suivants : reprendre les données du parcours précédent
                val distancePrecedente = tabDistances[tabDistances.indexOf(distance) - 1]
                val noeudsFromPrevious = tempDistanceRepository.getNoeudsFromPreviousParcours(distancePrecedente)
                noeudsAVisiter.addAll(noeudsFromPrevious)

                if (noeudsAVisiter.isEmpty()) {
                    return
                }

                tempDistanceRepository.deleteByPeiAndDistance(depart, distancePrecedente)
            }

            // Parcours des nœuds
            while (noeudsAVisiter.isNotEmpty()) {
                val noeudCourant = noeudsAVisiter[0]
                noeudsVisites.add(noeudCourant)
                try {
                    val courantRecord = tempDistanceRepository.getByPeiAndSommet(depart, noeudCourant)
                    var voieCourante: UUID? = null
                    if (courantRecord == null) {
                        voieCourante = reseauRepository.getIdTronconPei(depart)
                    }
                    exploreVoisins(
                        noeudCourant, voieCourante, depart, idReseauImporte,
                        distance, profondeurCouverture, useReseauImporteWithCourant,
                        courantRecord, noeudsAVisiter, noeudsVisites, debutChemin,
                    )
                } catch (_: Exception) {
                    // Continue avec le nœud suivant au lieu d'arrêter tout le parcours
                    logger.info("Pas de voisin à visiter pour le noeud: $noeudCourant")
                }
                debutChemin = false
                noeudsAVisiter.removeAt(0)
            }
            // Sauvegarde de la couverture calculée
            saveCouverture(depart, idEtude, distance)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Parcours des voisins d'un nœud
     */
    private fun exploreVoisins(
        noeudCourant: UUID,
        voieCourante: UUID?,
        depart: UUID,
        idReseauImporte: UUID?,
        distance: Int,
        profondeurCouverture: Int,
        useReseauImporteWithCourant: Boolean,
        courantRecord: TempDistance?,
        noeudsAVisiter: MutableList<UUID>,
        noeudsVisites: List<UUID>,
        debutChemin: Boolean,
    ) {
        val voieCalcul = courantRecord?.tempDistanceVoieCourante ?: voieCourante
        if (voieCalcul == null) {
            logger.warn("WARN: Pas de voie courante pour calculer les voies latérales")
            return
        }
        voiesLateralesUseCase.computeVoiesLaterales(
            voieCalcul,
            noeudCourant,
            idReseauImporte,
            useReseauImporteWithCourant,
        )
        val voieGauche = voiesLateralesUseCase.getVoieGauche()
        val voieDroite = voiesLateralesUseCase.getVoieDroite()
        // Récupération des voisins exactement comme dans le SQL
        val voisinsSortants = reseauRepository.getTronconsSortants(
            noeudCourant,
            idReseauImporte,
            useReseauImporteWithCourant,
        )

        val voisinsEntrants = reseauRepository.getTronconsEntrants(
            noeudCourant,
            idReseauImporte,
            useReseauImporteWithCourant,
        )

        // Mapper exactement comme le SQL avec inversion de géométrie pour les entrants
        val voisins = voisinsSortants.map { record ->
            VoisinInfo(
                reseauId = record.reseauId,
                destination = record.reseauSommetDestination,
                source = record.reseauSommetSource,
                distance = (record.reseauGeometrie as LineString).length,
                geometrie = record.reseauGeometrie as LineString,
                peiTroncon = record.reseauPeiTroncon,
                traversable = record.reseauTraversable ?: true,
                niveau = record.reseauNiveau ?: 0,
            )
        } + voisinsEntrants.map { record ->
            VoisinInfo(
                reseauId = record.reseauId,
                destination = record.reseauSommetSource, // source devient destination
                source = record.reseauSommetSource, // garder source comme source (comme dans SQL)
                distance = (record.reseauGeometrie as LineString).length,
                geometrie = geometrieUseCase.reverseLineString(record.reseauGeometrie as LineString), // Inverser la géométrie
                peiTroncon = record.reseauPeiTroncon,
                traversable = record.reseauTraversable ?: true,
                niveau = record.reseauNiveau ?: 0,
            )
        }

        // Filtrer selon voies latérales
        val voisinsFiltres = voisins.filter { voisin ->
            val estVoieLaterale = voiesLateralesUseCase.isVoieLaterale(voisin.reseauId)
            val aucuneVoieLaterale = voiesLateralesUseCase.aucuneVoieLaterale()
            estVoieLaterale || aucuneVoieLaterale
        }

        // Variable locale pour debutChemin qui sera modifiée dans la boucle
        var debutCheminLocal = debutChemin

        for (voisin in voisinsFiltres) {
            if (shouldIgnoreVoisin(voisin, courantRecord, debutCheminLocal)) {
                continue
            }

            val voisinLateral = voiesLateralesUseCase.getVoieLaterale(voisin.reseauId)

            if (voisinLateral != null && voisinLateral.voieLateraleAccessible == false) {
                continue
            }

            if (voieGauche != null && voieDroite != null && voieGauche.voieLateraleVoieVoisine != voieDroite.voieLateraleVoieVoisine) {
                // Si c'est une voie à gauche, que la voie courante est non traversable,
                // qu'on trace le buffer sur la droite et que ce n'est pas la première voie non traversable à droite
                val premiereVoieNonTraversableDroite = voiesLateralesUseCase.getFirstVoieNonTraversable("DESC")
                if (voisinLateral?.voieLateraleGauche == true &&
                    courantRecord?.tempDistanceTraversable == false &&
                    courantRecord.tempDistanceSide == TypeSide.RIGHT &&
                    premiereVoieNonTraversableDroite?.voieLateraleVoieVoisine != voisinLateral.voieLateraleVoieVoisine
                ) {
                    continue
                }

                // Si c'est une voie à droite, que la voie courante est non traversable,
                // qu'on trace le buffer sur la gauche et que ce n'est pas la première voie non traversable à gauche
                val premiereVoieNonTraversableGauche = voiesLateralesUseCase.getFirstVoieNonTraversable("ASC")
                if (voisinLateral?.voieLateraleDroite == true &&
                    courantRecord?.tempDistanceTraversable == false &&
                    courantRecord.tempDistanceSide == TypeSide.LEFT &&
                    premiereVoieNonTraversableGauche?.voieLateraleVoieVoisine != voisinLateral.voieLateraleVoieVoisine
                ) {
                    continue
                }
            }

            val distanceParcourue = computeDistanceParcourue(voisin, courantRecord)

            // Gestion des voies trop longues : troncature (équivalent SQL v2)
            var voisinAjuste = voisin
            var distanceFinale = distanceParcourue
            var bufferEndPoint: Point? = null

            if (distanceParcourue > distance && (courantRecord?.tempDistanceDistance ?: 0.0) < distance) {
                // La voie dépasse la limite mais on peut en parcourir une partie
                val fraction = when {
                    distanceParcourue <= distance -> 1.0
                    else -> (1.0 - ((distanceParcourue - distance) / voisin.distance))
                }

                val geometrieTronquee = geometrieUseCase.lineSubstring(voisin.geometrie, 0.0, fraction)
                voisinAjuste = voisin.copy(
                    geometrie = geometrieTronquee,
                    distance = geometrieTronquee.length,
                )
                // Pour la géométrie tronquée, on prend le dernier point
                bufferEndPoint = geometrieTronquee.endPoint
                distanceFinale = distance.toDouble()
            } else {
                // Récupérer le point de fin seulement si pas début de chemin ET destination non null
                if (!debutCheminLocal && voisin.destination != null) {
                    bufferEndPoint = sommetRepository.getPoint(voisin.destination)
                }
            }

            // Vérification de l'existence du trajet et de la distance
            val existeDeja = tempDistanceRepository.checkIfExistTrajet(
                depart,
                voisin.destination,
                voisin.reseauId,
            )

            if (!existeDeja && distanceFinale <= distance) {
                val buffer = createBuffer(
                    voisinAjuste, // Utiliser voisinAjuste au lieu de voisin
                    courantRecord,
                    voieGauche,
                    voieDroite,
                    profondeurCouverture,
                    idReseauImporte,
                    useReseauImporteWithCourant,
                    bufferEndPoint,
                )

                saveTempDistance(
                    depart,
                    voisinAjuste,
                    distanceFinale,
                    buffer,
                    courantRecord?.tempDistanceVoieCourante ?: voieCourante!!,
                    courantRecord?.tempDistanceSide ?: TypeSide.BOTH,
                )
            }

            if (!noeudsVisites.contains(voisin.destination) && distanceFinale < distance && voisin.destination != null) {
                noeudsAVisiter.add(voisin.destination)
            }

            debutCheminLocal = false
        }
    }

    /**
     * Création du buffer pour une voie
     */
    private fun createBuffer(
        voisin: VoisinInfo,
        courantRecord: TempDistance?,
        voieGauche: VoieLaterale?,
        voieDroite: VoieLaterale?,
        profondeurCouverture: Int,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
        bufferEndPoint: Point? = null,
    ): Geometry {
        // Détermination du côté du buffer
        val (bufferSide, bufferEndCap) = determineParametresBuffer(
            voisin,
            courantRecord,
            voieGauche,
            voieDroite,
        )

        // Taille du buffer selon le niveau
        val tailleBuffer = if (voisin.niveau != 0) BUFFER_SIZE_RESTREINT else profondeurCouverture

        // Création du buffer initial
        var buffer = geometrieUseCase.createBuffer(
            voisin.geometrie,
            tailleBuffer.toDouble(),
            bufferSide,
            bufferEndCap,
        )

        // Découpage par les voies non traversables
        buffer = splitByVoiesNonTraversables(
            buffer,
            voisin,
            idReseauImporte,
            useReseauImporteWithCourant,
        ).getGeometryN(0) as Polygon

        // Ajout du buffer des sommets si nécessaire et si bufferEndPoint est fourni
        if (bufferSide != BUFFER_SIDE_BOTH && bufferEndPoint != null) {
            buffer = addBufferSommets(buffer, bufferEndPoint, profondeurCouverture, idReseauImporte, useReseauImporteWithCourant)
        }

        buffer.srid = appSettings.srid
        return buffer
    }

    private data class VoisinInfo(
        val reseauId: UUID,
        val destination: UUID?,
        val source: UUID?,
        val distance: Double,
        val geometrie: LineString,
        val peiTroncon: UUID?,
        val traversable: Boolean,
        val niveau: Int,
    )

    private fun determineParametresBuffer(
        voisin: VoisinInfo,
        courantRecord: TempDistance?,
        voieGauche: VoieLaterale?,
        voieDroite: VoieLaterale?,
    ): Pair<String, String> {
        val bufferSide: String
        val bufferEndCap: String

        when {
            courantRecord?.tempDistanceTraversable == false && !voisin.traversable -> {
                bufferSide = courantRecord.tempDistanceSide?.name ?: "both"
                bufferEndCap = "round"
            }
            voisin.traversable -> {
                bufferSide = "both"
                bufferEndCap = "round"
            }
            voieGauche?.voieLateraleVoieVoisine == voieDroite?.voieLateraleVoieVoisine -> {
                bufferSide = courantRecord?.tempDistanceSide?.name ?: "both"
                bufferEndCap = "round"
            }
            voisin.reseauId == voieGauche?.voieLateraleVoieVoisine -> {
                bufferSide = if (courantRecord?.tempDistanceSide?.name == BUFFER_SIDE_BOTH) {
                    "left"
                } else courantRecord?.tempDistanceSide?.name ?: "left"
                bufferEndCap = "flat"
            }
            voisin.reseauId == voieDroite?.voieLateraleVoieVoisine -> {
                bufferSide = if (courantRecord?.tempDistanceSide?.name == BUFFER_SIDE_BOTH) {
                    "right"
                } else courantRecord?.tempDistanceSide?.name ?: "right"
                bufferEndCap = "flat"
            }
            else -> {
                bufferSide = "both"
                bufferEndCap = "round"
            }
        }

        return Pair(bufferSide, bufferEndCap)
    }

    private fun saveCouverture(depart: UUID, idEtude: UUID, distance: Int) {
        // Suppression de l'ancienne couverture
        couvertureTraceePeiRepository.delete(distance, depart, idEtude)

        // Union incrémentale des géométries temporaires (comme en SQL)
        val geometries = tempDistanceRepository.getGeometries(depart)
        var geometrieUnion: Geometry? = null

        for (geom in geometries) {
            geometrieUnion = geometrieUseCase.safeUnion(geometrieUnion, geom)
        }

        // Traitement des MultiPolygon vers Polygon (équivalent SQL v2)
        if (geometrieUnion != null && geometrieUnion.geometryType == "MultiPolygon") {
            try {
                val buffered = geometrieUseCase.createBuffer(geometrieUnion, BUFFER_DELTA_POS)
                geometrieUnion = geometrieUseCase.createBuffer(buffered, BUFFER_DELTA_NEG)
            } catch (e: Exception) {
                logger.warn("WARN: Échec de conversion MultiPolygon vers Polygon: ${e.message}")
            }
        }

        if (geometrieUnion != null && geometrieUnion.geometryType == "Polygon") {
            geometrieUnion.srid = appSettings.srid
            couvertureTraceePeiRepository.insert(distance, depart, idEtude, geometrieUnion)
        }
    }

    private fun emptyTablesTemporaires() {
        voiesLateralesUseCase.emptyTable()
        tempDistanceRepository.emptyTable()
    }

    // Méthodes utilitaires simplifiées pour l'exemple
    private fun shouldIgnoreVoisin(voisin: VoisinInfo, courantRecord: TempDistance?, debutChemin: Boolean): Boolean {
        return (voisin.peiTroncon != null && !debutChemin) ||
            (courantRecord != null && voisin.reseauId == courantRecord.tempDistanceVoieCourante)
    }

    private fun computeDistanceParcourue(voisin: VoisinInfo, courantRecord: TempDistance?): Double {
        return voisin.distance + (courantRecord?.tempDistanceDistance ?: 0.0)
    }

    private fun splitByVoiesNonTraversables(
        buffer: Geometry,
        voisin: VoisinInfo,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): Geometry {
        val voiesNonTraversables = reseauRepository.getTronconsNonTraversablesIntersectant(
            buffer,
            voisin.reseauId,
            idReseauImporte,
            useReseauImporteWithCourant,
        )

        if (voiesNonTraversables.isEmpty()) {
            return buffer
        }

        // Union de toutes les voies non traversables qui intersectent le buffer
        val bladeGeometries = voiesNonTraversables.map { it.reseauGeometrie }
        var blade: Geometry? = null

        for (voieGeom in bladeGeometries) {
            blade = if (blade == null) {
                voieGeom
            } else {
                geometrieUseCase.safeUnion(blade, voieGeom) ?: blade
            }
        }

        if (blade == null) {
            return buffer
        }

        // Découpage du buffer par les voies non traversables (équivalent ST_SPLIT du SQL)
        val splitResult = geometrieUseCase.split(buffer, blade)

        // Si le split a créé plusieurs géométries, on prend celle la plus proche du début de la voie
        return if (geometrieUseCase.getNumGeometries(splitResult) > 1) {
            val pointRef = geometrieUseCase.lineInterpolatePoint(voisin.geometrie, BUFFER_DELTA_POS)
            geometrieUseCase.getClosestGeometry(splitResult, pointRef) ?: buffer
        } else {
            splitResult
        }
    }

    private fun addBufferSommets(
        buffer: Geometry,
        endPoint: Point,
        profondeurCouverture: Int,
        idReseauImporte: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): Geometry {
        val bufferSommets = geometrieUseCase.createBuffer(endPoint, profondeurCouverture.toDouble())

        // Récupération des voies non traversables qui intersectent le buffer du sommet
        val voiesNonTraversables = reseauRepository.getTronconsNonTraversablesIntersectant(
            bufferSommets,
            UUID.randomUUID(),
            idReseauImporte,
            useReseauImporteWithCourant,
        )

        if (voiesNonTraversables.isEmpty()) {
            return geometrieUseCase.safeUnion(buffer, bufferSommets) ?: buffer
        }

        // Union de toutes les voies non traversables
        val bladeGeometries = voiesNonTraversables.map { it.reseauGeometrie }
        var bladeSommets: Geometry? = null

        for (voieGeom in bladeGeometries) {
            bladeSommets = if (bladeSommets == null) {
                voieGeom
            } else {
                geometrieUseCase.safeUnion(bladeSommets, voieGeom) ?: bladeSommets
            }
        }

        if (bladeSommets == null) {
            return geometrieUseCase.safeUnion(buffer, bufferSommets) ?: buffer
        }

        // Découpage du buffer des sommets
        val splitResult = geometrieUseCase.split(bufferSommets, bladeSommets)

        // On prend la partie qui a le plus d'intersection avec le buffer principal
        var bestGeom: Geometry? = null
        var maxIntersectionRatio = 0.0

        for (i in 0 until geometrieUseCase.getNumGeometries(splitResult)) {
            val geom = splitResult.getGeometryN(i)
            val intersection = geometrieUseCase.safeIntersection(geom, buffer)
            if (intersection != null && geom.area > 0) {
                val ratio = intersection.area / geom.area
                if (ratio > maxIntersectionRatio) {
                    maxIntersectionRatio = ratio
                    bestGeom = geom
                }
            }
        }

        return if (bestGeom != null) {
            geometrieUseCase.safeUnion(buffer, bestGeom) ?: buffer
        } else {
            buffer
        }
    }

    private fun saveTempDistance(
        depart: UUID,
        voisin: VoisinInfo,
        distanceParcourue: Double,
        buffer: Geometry,
        voiePrecedente: UUID,
        side: TypeSide,
    ) {
        // Gérer le cast MultiPolygon -> Polygon comme dans le SQL
        var bufferFinal = buffer
        if (buffer.geometryType == "MultiPolygon") {
            // Le SQL utilise ST_Dump pour prendre le premier polygon
            bufferFinal = buffer.getGeometryN(0)
        }

        tempDistanceRepository.insert(
            peiDepart = depart,
            sommet = voisin.destination!!,
            voieCourante = voisin.reseauId,
            voiePrecedente = voiePrecedente, // Utiliser voiePrecedente passée en paramètre
            distance = distanceParcourue,
            geometrie = bufferFinal,
            traversable = voisin.traversable,
            side = side,
        )
    }
}
