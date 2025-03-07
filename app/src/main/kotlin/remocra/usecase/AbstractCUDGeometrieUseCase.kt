package remocra.usecase

import jakarta.inject.Inject
import org.geotools.api.referencing.crs.CoordinateReferenceSystem
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.auth.UserInfo
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
        val sourceCRS = CRS.decode("EPSG:${input.srid}")
        val geom = JTS.transform(input, CRS.findMathTransform(sourceCRS, targetCRS))
            ?: throw IllegalArgumentException("Impossible de convertir la géometrie $input en ${targetCRS.name}")
        geom.srid = appSettings.srid
        return geom
    }

    override fun execute(userInfo: UserInfo?, element: T, mainTransactionManager: TransactionManager?): Result {
        val safeElement = ensureSrid(element)
        checkZoneCompetenceContainsUseCase.checkContains(userInfo, getListGeometrie(safeElement))
        return super.execute(userInfo, safeElement, mainTransactionManager)
    }
}
