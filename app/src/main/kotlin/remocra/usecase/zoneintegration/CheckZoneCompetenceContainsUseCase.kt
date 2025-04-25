package remocra.usecase.zoneintegration

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.util.GeometryCombiner
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.ZoneIntegrationRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.utils.toGeomFromText

/**
 * Vérifie si la liste de géométrie est dans la zone de compétence de l'utilisateur connecté
 */
class CheckZoneCompetenceContainsUseCase : AbstractUseCase() {

    @Inject private lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    fun checkContains(userInfo: WrappedUserInfo, geometries: Collection<Geometry>) {
        // Si c'est un super admin, on ne prend pas en compte la zone de compétence
        if (userInfo.isSuperAdmin) {
            return
        }

        if (userInfo.nom != GlobalConstants.UTILISATEUR_SYSTEME_USERNAME) {
            if (userInfo.zoneCompetence == null) {
                throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_INTROUVABLE_FORBIDDEN)
            }

            // On regroupe toutes les géométries fournies pour ne faire qu'un objet type Geometry
            val aggregatedGeometry = GeometryCombiner.combine(geometries)
            aggregatedGeometry.srid = geometries.first().srid

            if (!zoneIntegrationRepository.checkContains(
                    userInfo.zoneCompetence!!.zoneIntegrationId,
                    aggregatedGeometry.toGeomFromText(),
                )
            ) {
                throw RemocraResponseException(ErrorType.FORBIDDEN_ZONE_COMPETENCE)
            }
        }
    }
}
