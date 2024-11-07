package remocra.usecase

import jakarta.ws.rs.ForbiddenException
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.exception.RemocraResponseException

abstract class AbstractCUDGeometrieUseCase<T : Any>(override val typeOperation: TypeOperation) : AbstractCUDUseCase<T>(typeOperation) {
    /**
     * Vérifie si la zone de compétence de l'objet est dans la zone de compétence de l'utilisateur connecté
     */
    private fun checkZoneCompetence(userInfo: UserInfo?, element: T) {
        // TODO à retirer quand le userinfo ne sera plus potentiellement nul
        if (userInfo == null) {
            throw ForbiddenException()
        }

        // Si c'est un super admin, on ne prend pas en compte la zone de compétence
        if (userInfo.isSuperAdmin) {
            return
        }

        if (userInfo.username != GlobalConstants.UTILISATEUR_SYSTEME_USERNAME) {
            if (userInfo.zoneCompetence == null) {
                throw RemocraResponseException(ErrorType.ZONE_COMPETENCE_INTROUVABLE_FORBIDDEN)
            }

            // On récupère la zone de compétence de l'utilisateur connecté
            val zoneCompetenceGeometrie: Geometry = userInfo.zoneCompetence!!.zoneIntegrationGeometrie

            // On regarde si chacunes des géométries sont comprises dans la zone de l'utilisateur
            getListGeometrie(element).forEach {
                if (!(zoneCompetenceGeometrie as MultiPolygon).contains(it)) {
                    throw RemocraResponseException(ErrorType.FORBIDDEN_ZONE_COMPETENCE)
                }
            }
        }
    }

    /**
     * Récupère les géométries
     */
    protected abstract fun getListGeometrie(element: T): Collection<Geometry>

    override fun execute(userInfo: UserInfo?, element: T, mainTransactionManager: TransactionManager?): Result {
        checkZoneCompetence(userInfo, element)
        return super.execute(userInfo, element, mainTransactionManager)
    }
}
