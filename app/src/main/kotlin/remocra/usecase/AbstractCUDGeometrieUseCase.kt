package remocra.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.usecase.zoneintegration.CheckZoneCompetenceContainsUseCase

abstract class AbstractCUDGeometrieUseCase<T : Any>(override val typeOperation: TypeOperation) : AbstractCUDUseCase<T>(typeOperation) {

    @Inject private lateinit var checkZoneCompetenceContainsUseCase: CheckZoneCompetenceContainsUseCase

    /**
     * Récupère les géométries
     */
    protected abstract fun getListGeometrie(element: T): Collection<Geometry>

    override fun execute(userInfo: UserInfo?, element: T, mainTransactionManager: TransactionManager?): Result {
        checkZoneCompetenceContainsUseCase.checkContains(userInfo, getListGeometrie(element))
        return super.execute(userInfo, element, mainTransactionManager)
    }
}
