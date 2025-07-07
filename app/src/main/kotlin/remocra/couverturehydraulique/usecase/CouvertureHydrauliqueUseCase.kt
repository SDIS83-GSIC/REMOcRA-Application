package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Service principal pour le calcul de la couverture hydraulique
 */
class CouvertureHydrauliqueUseCase @Inject constructor(
    private val zonageUseCase: ZonageUseCase,

    private val parcoursUseCase: ParcoursUseCase,
) : AbstractUseCase() {

    /**
     * Fonction principale équivalente à couverture_hydraulique_zonage
     */
    fun calculerCouvertureHydrauliqueZonage(
        idEtude: UUID,
        isodistances: List<Int>,
        profondeurCouverture: Int,
    ): Int {
        val tabDistances = isodistances.map { it - profondeurCouverture }.sorted().toIntArray()

        // Tracé des zones d'isodistances
        for (dist in tabDistances) {
            zonageUseCase.traceZoneIsodistance(idEtude, dist)
        }

        // Calcul des zones de risque
        zonageUseCase.calculeZonesRisque(idEtude)

        return 1
    }

    /**
     * Fonction principale équivalente à parcours_couverture_hydraulique
     */
    fun parcoursCouvertureHydraulique(
        depart: UUID,
        idEtude: UUID,
        idReseauImporte: UUID?,
        isodistances: List<Int>,
        profondeurCouverture: Int,
        useReseauImporteWithCourant: Boolean,
    ): Int {
        val tabDistances = isodistances.map { it - profondeurCouverture }.sorted().toIntArray()

        return parcoursUseCase.executeParcours(
            depart,
            idEtude,
            idReseauImporte,
            tabDistances,
            profondeurCouverture,
            useReseauImporteWithCourant,
        )
    }
}
