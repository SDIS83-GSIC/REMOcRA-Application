package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import remocra.app.ParametresProvider
import remocra.data.couverturehydraulique.ClosestPeiData
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.CouvertureHydrauliqueCalculRepository
import remocra.db.PeiRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase

class ClosestPeiUseCase : AbstractUseCase() {

    @Inject
    lateinit var couvertureHydrauliqueCalculRepository: CouvertureHydrauliqueCalculRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    fun execute(element: ClosestPeiData): CouvertureHydrauliqueCalculRepository.ClosestPeiResult? {
        val distanceMaxParcours = parametresProvider.getParametreInt(ParametreEnum.DECI_DISTANCE_MAX_PARCOURS.name)
            ?: throw RemocraResponseException(ErrorType.CALCUL_COUVERTURE_DECI_DISTANCE_MAX_PARCOURS_MANQUANT)

        // On transforme les coordonnées du point fait par l'utilisateur en géométrie
        val geomPoint = GeometryFactory(PrecisionModel(), element.srid).createPoint(
            Coordinate(
                element.longitude,
                element.latitude,
            ),
        )

        val closestPeiResult = couvertureHydrauliqueCalculRepository.getClosestPei(
            geomPoint,
            element.srid,
            distanceMaxParcours,
        )

        if (closestPeiResult?.pei != null) {
            // On regarde si c'est un PEI en projet
            val isPeiProjet = couvertureHydrauliqueCalculRepository.checkIsPeiProjet(closestPeiResult.pei)
            closestPeiResult.peiGeometry = if (isPeiProjet) {
                couvertureHydrauliqueCalculRepository.getGeometriePeiProjet(closestPeiResult.pei)
            } else {
                peiRepository.getGeometriePei(closestPeiResult.pei)
            }
        }

        return closestPeiResult
    }
}
