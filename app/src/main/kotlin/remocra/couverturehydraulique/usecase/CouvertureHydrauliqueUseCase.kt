package remocra.couverturehydraulique.usecase

import jakarta.inject.Inject
import remocra.couverturehydraulique.db.PeiRepository
import remocra.couverturehydraulique.db.PeiRepository.PeiCouvertureHydraulique
import remocra.couverturehydraulique.graphe.Graphe
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Service principal pour le calcul de la couverture hydraulique
 */
class CouvertureHydrauliqueUseCase @Inject constructor(
    private val zonageUseCase: ZonageUseCase,
    private val parcoursUseCase: ParcoursUseCase,
    private val peiRepository: PeiRepository,
) : AbstractUseCase() {

    /**
     * Fonction principale équivalente à couverture_hydraulique_zonage
     */
    fun calculerCouvertureHydrauliqueZonage(
        idEtude: UUID,
        isodistances: List<Int>,
        profondeurCouverture: Int,
        graphe: Graphe,
    ): Int {
        val tabDistances = isodistances.map { it - profondeurCouverture }.sorted().toIntArray()

        // Tracé des zones d'isodistances
        for (dist in tabDistances) {
            zonageUseCase.traceZoneIsodistance(idEtude, dist)
        }

        // Calcul des zones de risque
        zonageUseCase.calculateRiskZones(idEtude, graphe)

        return 1
    }

    /**
     * Fonction principale équivalente à parcours_couverture_hydraulique
     */
    fun parcoursCouvertureHydraulique(
        listePeiIdWithProjets: Set<UUID>,
        idEtude: UUID,
        distance: Int,
        profondeurCouverture: Int,
        graphe: Graphe,
    ): Int {
        val listePei = listePeiIdWithProjets
            .mapNotNull { peiRepository.getById(it) }
            .sortedWith(compareBy<PeiCouvertureHydraulique> { it.peiGeometrie.x }.thenBy { it.peiGeometrie.y })

        return parcoursUseCase.executeParcours(
            listePei,
            idEtude,
            distance,
            profondeurCouverture,
            graphe,
        )
    }
}
