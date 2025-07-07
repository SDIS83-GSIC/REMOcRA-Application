package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.couverturehydraulique.db.CouvertureTraceePeiRepository
import remocra.couverturehydraulique.db.CouvertureTraceeRepository
import remocra.couverturehydraulique.db.ParametreRepository
import remocra.usecase.AbstractUseCase
import remocra.usecase.couverturehydraulique.GeometrieUseCase
import java.util.UUID

/**
 * Service pour le calcul des zones de couverture hydraulique et des risques
 * Équivalent de la fonction couverture_hydraulique_zonage
 */
class ZonageUseCase @Inject constructor(
    private val geometrieUseCase: GeometrieUseCase,
    private val couvertureTraceeRepository: CouvertureTraceeRepository,
    private val couvertureTraceePeiRepository: CouvertureTraceePeiRepository,
    private val parametreRepository: ParametreRepository,
    private val appSettings: AppSettings,
) : AbstractUseCase() {

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
            couvertureDistance = geometrieUseCase.safeUnion(
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
    fun calculeZonesRisque(idEtude: UUID) {
        // Vérification s'il existe une fonction spécifique par SDIS
        val codeSdis = appSettings.codeSdis

        if (parametreRepository.existsFonctionSpecifiqueSdis(codeSdis)) {
            parametreRepository.executeFonctionSpecifiqueSdis(codeSdis, idEtude)
        } else {
            calculeZonesRisqueParDefaut(idEtude)
        }
    }

    /**
     * Calcul des zones de risque par défaut
     */
    private fun calculeZonesRisqueParDefaut(idEtude: UUID) {
        calculeRisqueCourantFaible(idEtude)
        calculeRisqueCourantOrdinaire(idEtude)
        calculeRisqueCourantImportant(idEtude)
        calculeRisqueParticulier(idEtude)
    }

    /**
     * Tracé du risque courant faible
     * Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
     */
    private fun calculeRisqueCourantFaible(idEtude: UUID) {
        var couvertureRisqueCourantFaible: Geometry? = null

        val couverturesPei = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(100, idEtude, appSettings.codeSdis)

        for (couverturePei in couverturesPei) {
            couvertureRisqueCourantFaible = geometrieUseCase.safeUnion(
                couvertureRisqueCourantFaible,
                couverturePei.couvertureTraceePeiGeometrie,
            )
        }

        saveZoneRisque(idEtude, "risque_courant_faible", couvertureRisqueCourantFaible)
    }

    /**
     * Tracé du risque courant ordinaire
     * Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m
     */
    private fun calculeRisqueCourantOrdinaire(idEtude: UUID) {
        var couvertureRisqueCourantOrdinaire: Geometry? = null

        val couverturesPei100m = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(100, idEtude, appSettings.codeSdis)
        val couverturesPei300m = couvertureTraceePeiRepository.getCouverturesNonGrosDebit(300, idEtude, appSettings.codeSdis)

        for (couverturePei in couverturesPei100m) {
            for (couvertureVoisin in couverturesPei300m) {
                if (couverturePei.couvertureTraceePeiId != couvertureVoisin.couvertureTraceePeiId) {
                    val intersection = geometrieUseCase.safeIntersection(
                        couverturePei.couvertureTraceePeiGeometrie,
                        couvertureVoisin.couvertureTraceePeiGeometrie,
                    )
                    couvertureRisqueCourantOrdinaire = geometrieUseCase.safeUnion(
                        couvertureRisqueCourantOrdinaire,
                        intersection,
                    )
                }
            }
        }

        saveZoneRisque(idEtude, "risque_courant_ordinaire", couvertureRisqueCourantOrdinaire)
    }

    /**
     * Tracé du risque courant important
     * À ce stade, identique au risque courant ordinaire
     */
    private fun calculeRisqueCourantImportant(idEtude: UUID) {
        val couvertureOrdinaire = couvertureTraceeRepository.getGeometrieByLabelAndEtude(
            "risque_courant_ordinaire",
            idEtude,
        )

        saveZoneRisque(idEtude, "risque_courant_important", couvertureOrdinaire)
    }

    /**
     * Tracé du risque particulier
     * Conditions: Intersection distances 50m et 250m, au moins un des deux PEI gros débit
     */
    private fun calculeRisqueParticulier(idEtude: UUID) {
        var couvertureRisqueParticulier: Geometry? = null

        // Étape 1: couverture 50m d'un gros débit avec une couverture 250m (tous PEI)
        val couverturesGrosDebit50m = couvertureTraceePeiRepository.getCouverturesGrosDebit(50, idEtude, appSettings.codeSdis)
        val couvertures250m = couvertureTraceePeiRepository.getByDistanceAndEtude(250, idEtude)

        for (couvertureGrosDebit in couverturesGrosDebit50m) {
            for (couvertureVoisin in couvertures250m) {
                // Vérifier que ce n'est pas le même PEI et qu'ils sont dans un rayon de 1000m
                if (couvertureGrosDebit.couvertureTraceePeiId != couvertureVoisin.couvertureTraceePeiId &&
                    couvertureGrosDebit.couvertureTraceePeiGeometrie != null && couvertureGrosDebit.couvertureTraceePeiGeometrie!!.distance(
                        couvertureVoisin.couvertureTraceePeiGeometrie,
                    ) <= 1000.0
                ) {
                    val intersection = geometrieUseCase.safeIntersection(
                        couvertureGrosDebit.couvertureTraceePeiGeometrie,
                        couvertureVoisin.couvertureTraceePeiGeometrie,
                    )
                    couvertureRisqueParticulier = geometrieUseCase.safeUnion(
                        couvertureRisqueParticulier,
                        intersection,
                    )
                }
            }
        }

        // Étape 2: couverture 250m d'un gros débit avec une couverture 50m (tous PEI)
        val couverturesGrosDebit250m = couvertureTraceePeiRepository.getCouverturesGrosDebit(250, idEtude, appSettings.codeSdis)
        val couvertures50m = couvertureTraceePeiRepository.getByDistanceAndEtude(50, idEtude)

        for (couvertureGrosDebit in couverturesGrosDebit250m) {
            for (couvertureVoisin in couvertures50m) {
                // Vérifier que ce n'est pas le même PEI et qu'ils sont dans un rayon de 1000m
                if (couvertureGrosDebit.couvertureTraceePeiId != couvertureVoisin.couvertureTraceePeiId &&
                    couvertureGrosDebit.couvertureTraceePeiGeometrie != null && couvertureGrosDebit.couvertureTraceePeiGeometrie!!.distance(
                        couvertureVoisin.couvertureTraceePeiGeometrie,
                    ) <= 1000.0
                ) {
                    val intersection = geometrieUseCase.safeIntersection(
                        couvertureGrosDebit.couvertureTraceePeiGeometrie,
                        couvertureVoisin.couvertureTraceePeiGeometrie,
                    )
                    couvertureRisqueParticulier = geometrieUseCase.safeUnion(
                        couvertureRisqueParticulier,
                        intersection,
                    )
                }
            }
        }

        saveZoneRisque(idEtude, "risque_particulier", couvertureRisqueParticulier)
    }

    private fun saveZoneRisque(idEtude: UUID, label: String, geometrie: Geometry?) {
        geometrie?.srid = appSettings.srid
        // Suppression de l'ancienne zone
        couvertureTraceeRepository.deleteByLabelAndEtude(label, idEtude)

        // Insertion de la nouvelle zone si elle existe
        couvertureTraceeRepository.insert(label, idEtude, geometrie)
    }
}
