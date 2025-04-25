package remocra.usecase

import jakarta.inject.Inject
import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.usecase.zoneintegration.CheckZoneCompetenceContainsUseCase

abstract class AbstractCUDGeometrieUseCase<T : Any>(override val typeOperation: TypeOperation) : AbstractCUDUseCase<T>(typeOperation) {

    @Inject protected lateinit var appSettings: AppSettings

    @Inject private lateinit var checkZoneCompetenceContainsUseCase: CheckZoneCompetenceContainsUseCase

    private val targetCRS: CoordinateReferenceSystem
        get() = CRS.decode(appSettings.epsg.name)

    /**
     * Récupère les géométries
     */
    protected abstract fun getListGeometrie(element: T): Collection<Geometry>

    /**
     * Assure que les géométries sont bien dans le système de coordonnées désiré
     */
    protected abstract fun ensureSrid(element: T): T

    /**
     * Utilitaire embarqué pour transformer une géométrie
     */
    protected fun transform(input: Geometry): Geometry {
        return remocra.utils.transform(input, targetCRS, appSettings.srid)
    }

    override fun execute(userInfo: WrappedUserInfo, element: T, mainTransactionManager: TransactionManager?): Result {
        val safeElement = ensureSrid(element)
        val geometrie = getListGeometrie(safeElement)
        if (geometrie.isNotEmpty()) {
            checkZoneCompetenceContainsUseCase.checkContains(userInfo, geometrie)
        }
        return super.execute(userInfo, safeElement, mainTransactionManager)
    }
}
