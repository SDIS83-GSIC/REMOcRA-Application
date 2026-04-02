package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.app.AppSettings
import remocra.couverturehydraulique.db.PeiRepository
import remocra.couverturehydraulique.db.ReseauRepository
import remocra.couverturehydraulique.db.SommetRepository
import remocra.db.jooq.couverturehydraulique.tables.pojos.Reseau
import remocra.usecase.AbstractUseCase
import remocra.usecase.couverturehydraulique.GeometrieUseCase
import java.util.UUID

/**
 * UseCase pour la gestion du réseau et des jonctions PEI
 * Équivalent des fonctions inserer_jonction_pei et retirer_jonction_pei
 */
class ReseauUseCase @Inject constructor(
    private val geometrieUseCase: GeometrieUseCase,
    private val reseauRepository: ReseauRepository,
    private val sommetRepository: SommetRepository,
    private val peiRepository: PeiRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {

    companion object {
        private const val FRACTION_MIN = 0.00001
        private const val FRACTION_MAX = 0.99999
    }

    // Suivi des tronçons créés pour chaque PEI (clé = peiId)
    private val tronconsCreesParPei = mutableMapOf<UUID, MutableList<UUID>>()

    private fun registerTronconForPei(peiId: UUID, tronconId: UUID) {
        tronconsCreesParPei.computeIfAbsent(peiId) { mutableListOf() }.add(tronconId)
    }

    fun getAndClearTronconsCrees(peiId: UUID): List<UUID> {
        val list = tronconsCreesParPei[peiId]?.toList().orEmpty()
        tronconsCreesParPei.remove(peiId)
        return list
    }

    /**
     * Insère une jonction PEI sur le réseau hydraulique.
     *
     * @param peiId Identifiant du PEI à connecter.
     * @param distanceMaxAuReseau Distance maximale pour la jonction.
     * @param idEtude Identifiant de l'étude concernée.
     * @param idReseau Identifiant du réseau (si utilisation du réseau importé seul).
     * @param useReseauImporteWithCourant Mode d'utilisation du réseau importé et courant.
     * @return `true` si la jonction a été créée, `false` sinon (pei trop loin du réseau).
     */
    fun insertJonctionPei(
        peiId: UUID,
        distanceMaxAuReseau: Int,
        idEtude: UUID,
        idReseau: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): Boolean {
        val pei = peiRepository.getById(peiId) ?: return false
        val jonction = findJonctionReseau(pei, distanceMaxAuReseau, idReseau, useReseauImporteWithCourant)
            ?: return false

        val idEtudePourJonction = when {
            useReseauImporteWithCourant -> idEtude // mode courant ou courant+importé
            idReseau != null -> idReseau // mode importé
            else -> null // mode commun
        }

        return when {
            // Jonction au milieu du tronçon
            jonction.fraction in FRACTION_MIN..FRACTION_MAX -> {
                splitTroncon(jonction, idEtudePourJonction, idReseau, useReseauImporteWithCourant, peiId)
            }
            // Jonction à une extrémité
            else -> {
                createJonctionExtremite(
                    jonction,
                    pei,
                    idEtudePourJonction,
                    idReseau,
                    useReseauImporteWithCourant,
                )
            }
        }
    }

    /**
     * Retire la jonction PEI du réseau.
     *
     * On ne supprime plus aucun tronçon existant (commun ou importé).
     * Le nettoyage principal (suppression des tronçons créés pour la jonction)
     * est fait via getAndClearTronconsCrees() dans CalculCouvertureUseCase.
     */
    fun removeJonctionPei(idPei: UUID): Boolean {
        reseauRepository.getByPei(idPei) ?: return false

        // Nettoyage systématique : on remet à NULL tous les champs reseau_pei_troncon pour ce PEI
        reseauRepository.resetPeiTronconByPei(idPei)

        return true
    }

    fun resetPeiTronconForEtude(etudeId: UUID) {
        reseauRepository.resetPeiTronconByEtude(etudeId)
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
     * Recherche la jonction la plus proche sur le réseau pour un PEI donné.
     *
     * @param pei Objet PEI à connecter.
     * @param distanceMax Distance maximale pour la jonction.
     * @param idReseau Identifiant du réseau (si utilisation du réseau importé seul).
     * @param useReseauImporteWithCourant Mode d'utilisation du réseau importé et courant.
     * @return Informations sur la jonction trouvée ou `null` si aucune jonction possible.
     */
    private fun findJonctionReseau(
        pei: PeiRepository.PeiCouvertureHydraulique,
        distanceMax: Int,
        idReseau: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): JonctionInfo? {
        val peiGeom = pei.peiGeometrie

        val reseauProche = reseauRepository.findTronconPlusProche(
            peiGeom,
            distanceMax,
            idReseau,
            useReseauImporteWithCourant,
        ) ?: return null

        val tronconGeom = reseauProche.reseauGeometrie as LineString
        val pointProche = computePointPlusProcheSurLigne(tronconGeom, peiGeom)
        val fraction = computeFractionOnLine(tronconGeom, pointProche)
        val distance = peiGeom.distance(pointProche)

        return JonctionInfo(
            tronconId = reseauProche.reseauId,
            jonctionGeometrie = pointProche,
            fraction = fraction,
            peiGeometrie = peiGeom,
            tronconGeometrie = tronconGeom,
            distance = distance,
        )
    }

    /**
     * Calcule le point le plus proche sur une LineString (équivalent ST_ClosestPoint)
     */
    private fun computePointPlusProcheSurLigne(ligne: LineString, point: Point): Point {
        var minDistance = Double.MAX_VALUE
        var closestPoint: Coordinate? = null

        val coordinates = ligne.coordinates
        for (i in 0 until coordinates.size - 1) {
            val segmentClosest = geometrieUseCase.calculateClosestPointOnSegment(
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
     * @param idEtude Identifiant de l'étude.
     * @param idReseau Identifiant du réseau (si utilisation du réseau importé seul).
     * @param useReseauImporteWithCourant Mode d'utilisation du réseau importé et courant.
     * @param peiId Identifiant du PEI.
     * @return `true` si l'opération a réussi, `false` sinon.
     */
    private fun splitTroncon(jonction: JonctionInfo, idEtude: UUID?, idReseau: UUID?, useReseauImporteWithCourant: Boolean, peiId: UUID): Boolean {
        val tronconOriginal = reseauRepository.getById(jonction.tronconId, useReseauImporteWithCourant, idReseau) ?: return false

        // On NE MODIFIE JAMAIS le tronçon d'origine (importé ou courant)
        // On crée TOUJOURS deux nouveaux tronçons pour l'étude courante
        val premierePartie = geometrieUseCase.lineSubstring(
            tronconOriginal.reseauGeometrie as LineString,
            0.0,
            jonction.fraction,
        )
        val deuxiemePartie = geometrieUseCase.lineSubstring(
            jonction.tronconGeometrie,
            jonction.fraction,
            1.0,
        )
        val nouvelleVoieId1 = reseauRepository.insert(
            geometrie = premierePartie,
            idEtude = idEtude,
            traversable = tronconOriginal.reseauTraversable,
            sensUnique = tronconOriginal.reseauSensUnique,
            niveau = tronconOriginal.reseauNiveau,
        )
        registerTronconForPei(peiId, nouvelleVoieId1)
        val nouvelleVoieId2 = reseauRepository.insert(
            geometrie = deuxiemePartie,
            idEtude = idEtude,
            traversable = tronconOriginal.reseauTraversable,
            sensUnique = tronconOriginal.reseauSensUnique,
            niveau = tronconOriginal.reseauNiveau,
        )
        registerTronconForPei(peiId, nouvelleVoieId2)
        // Création du tronçon PEI (St_MakeLine entre le PEI et la jonction)
        val sommetSourceId = sommetRepository.getByGeometrieTopologie(jonction.peiGeometrie, idEtude)?.sommetId
            ?: sommetRepository.ensureSommet(jonction.peiGeometrie, idEtude)
        val sommetDestinationId = sommetRepository.getByGeometrieTopologie(jonction.jonctionGeometrie, idEtude)?.sommetId
            ?: sommetRepository.ensureSommet(jonction.jonctionGeometrie, idEtude)
        val jonctionTronconId = reseauRepository.insert(
            geometrie = geometrieUseCase.makeLine(jonction.peiGeometrie, jonction.jonctionGeometrie),
            idEtude = idEtude,
            peiTroncon = peiId,
            sommetSource = sommetSourceId,
            sommetDestination = sommetDestinationId,
        )
        registerTronconForPei(peiId, jonctionTronconId)
        // Création du sommet de jonction et des connexions
        createSommetsAndConnexionsSplit(jonction, tronconOriginal, nouvelleVoieId1, nouvelleVoieId2, jonctionTronconId, idEtude)
        return true
    }

    /**
     * Crée une jonction à une extrémité du tronçon.
     *
     * @param jonction Informations sur la jonction.
     * @param pei Objet PEI à connecter.
     * @param idEtude Identifiant de l'étude.
     * @param idReseau Identifiant du réseau (si utilisation du réseau importé seul).
     * @param useReseauImporteWithCourant Mode d'utilisation du réseau importé et courant.
     * @return `true` si la jonction a été créée, `false` sinon.
     */
    private fun createJonctionExtremite(
        jonction: JonctionInfo,
        pei: PeiRepository.PeiCouvertureHydraulique,
        idEtude: UUID?,
        idReseau: UUID?,
        useReseauImporteWithCourant: Boolean,
    ): Boolean {
        val tronconOriginal = reseauRepository.getById(jonction.tronconId, useReseauImporteWithCourant, idReseau)

        val extremiteSummit = when {
            tronconOriginal == null -> null
            jonction.fraction <= FRACTION_MIN -> tronconOriginal.reseauSommetSource
            jonction.fraction >= 1.0 - FRACTION_MIN -> tronconOriginal.reseauSommetDestination
            else -> null
        }

        val sommetJonctionId = extremiteSummit
            ?: sommetRepository.getByGeometrieTopologie(jonction.jonctionGeometrie, idEtude)?.sommetId
            ?: sommetRepository.ensureSommet(jonction.jonctionGeometrie, idEtude)

        val sommetPeiId = sommetRepository.getByGeometrie(pei.peiGeometrie)?.sommetId
            ?: sommetRepository.ensureSommet(pei.peiGeometrie, idEtude)

        val tronconJonctionId = reseauRepository.insert(
            geometrie = geometrieUseCase.makeLine(
                pei.peiGeometrie,
                jonction.jonctionGeometrie,
            ),
            idEtude = idEtude,
            peiTroncon = pei.peiId,
            sommetSource = sommetPeiId,
            sommetDestination = sommetJonctionId,
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
        // Implémentation simplifiée du calcul de fraction
        val longueurTotale = ligne.length
        var distanceParcourue = 0.0
        val coordinates = ligne.coordinates

        for (i in 0 until coordinates.size - 1) {
            val segmentClosest = geometrieUseCase.calculateClosestPointOnSegment(
                coordinates[i],
                coordinates[i + 1],
                point.coordinate,
            )

            if (point.coordinate.distance(segmentClosest) < 0.001) { // Tolerance
                val distanceSegment = coordinates[i].distance(segmentClosest)
                return (distanceParcourue + distanceSegment) / longueurTotale
            }

            distanceParcourue += coordinates[i].distance(coordinates[i + 1])
        }

        return 0.0
    }

    /**
     * Crée les sommets et met à jour les connexions après la séparation d'un tronçon.
     *
     * @param jonction Informations sur la jonction.
     * @param tronconOriginal Tronçon original avant séparation.
     * @param nouvelleVoieId Identifiant du nouveau tronçon créé.
     */
    private fun createSommetsAndConnexions(
        jonction: JonctionInfo,
        tronconOriginal: Reseau,
        nouvelleVoieId: UUID,
    ) {
        val sommetJonctionId = sommetRepository.ensureSommet(jonction.jonctionGeometrie)

        // Mise à jour des connexions
        reseauRepository.updateSommetDestination(jonction.tronconId, sommetJonctionId)

        reseauRepository.updateSommetSource(
            nouvelleVoieId,
            sommetSource = sommetJonctionId,
        )
        if (tronconOriginal.reseauSommetDestination != null) {
            reseauRepository.updateSommetDestination(
                nouvelleVoieId,
                sommetDestination = tronconOriginal.reseauSommetDestination!!,
            )
        }
    }

    /**
     * Crée les sommets et met à jour les connexions pour deux tronçons découpés et la jonction PEI.
     *
     * @param jonction Informations sur la jonction.
     * @param tronconOriginal Tronçon original avant découpe.
     * @param voieId1 Identifiant du premier tronçon créé (avant la jonction).
     * @param voieId2 Identifiant du second tronçon créé (après la jonction).
     * @param jonctionTronconId Identifiant du tronçon de jonction PEI.
     */
    private fun createSommetsAndConnexionsSplit(
        jonction: JonctionInfo,
        tronconOriginal: Reseau,
        voieId1: UUID,
        voieId2: UUID,
        jonctionTronconId: UUID,
        idEtude: UUID?,
    ) {
        // Création ou récupération du sommet de jonction
        val sommetJonctionId = sommetRepository.getByGeometrieTopologie(jonction.jonctionGeometrie, idEtude)?.sommetId
            ?: sommetRepository.ensureSommet(jonction.jonctionGeometrie, idEtude)
        val sommetPeiId = sommetRepository.getByGeometrieTopologie(jonction.peiGeometrie, idEtude)?.sommetId
            ?: sommetRepository.ensureSommet(jonction.peiGeometrie, idEtude)

        // Mise à jour des connexions pour les deux nouveaux tronçons
        // voie1 (avant jonction) : destination = sommetJonction, source = source d'origine
        reseauRepository.updateSommetDestination(voieId1, sommetJonctionId)
        tronconOriginal.reseauSommetSource?.let {
            reseauRepository.updateSommetSource(voieId1, it)
        }
        // voie2 (après jonction) : source = sommetJonction, destination = destination d'origine
        reseauRepository.updateSommetSource(voieId2, sommetJonctionId)
        tronconOriginal.reseauSommetDestination?.let {
            reseauRepository.updateSommetDestination(voieId2, it)
        }
        // tronçon de jonction PEI : source = sommetPei, destination = sommetJonction
        reseauRepository.updateSommetSource(jonctionTronconId, sommetPeiId)
        reseauRepository.updateSommetDestination(jonctionTronconId, sommetJonctionId)
    }

    fun deleteTroncon(tronconId: UUID) {
        reseauRepository.delete(tronconId)
    }

    /**
     * Fusionne deux géométries en une seule.
     *
     * @param geom1 Première géométrie.
     * @param geom2 Deuxième géométrie.
     * @return Géométrie fusionnée. Ou si échec, retourne la première géométrie (ISO SQL V2).
     */
    private fun mergeGeometries(geom1: Geometry, geom2: Geometry): Geometry {
        return try {
            geom1.union(geom2)
        } catch (_: Exception) {
            geom1
        }
    }
}
