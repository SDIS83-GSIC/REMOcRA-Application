package remocra.web.pei.import.validationstrategy

import jakarta.inject.Inject
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErreurImportPei
import remocra.db.PeiRepository
import remocra.usecase.zoneintegration.CheckZoneCompetenceContainsUseCase
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

class PeiZoneVerificationStrategy @Inject constructor(
    private val peiRepository: PeiRepository,
    private val appSettings: AppSettings,
    private val checkZoneCompetenceContainsUseCase: CheckZoneCompetenceContainsUseCase,
) : VerificationStrategy {

    private fun createPeiPoint(data: LigneImportPeiData, srid: Int): Point {
        val geometryFactory = GeometryFactory(PrecisionModel(), srid)
        var point = geometryFactory.createPoint(Coordinate(data.coordonneeX, data.coordonneeY))
        val sourceEpsg = "EPSG:${data.epsg}"
        val targetEpsg = appSettings.epsg.name

        if (sourceEpsg != targetEpsg) {
            try {
                val transform = CRS.findMathTransform(CRS.decode(sourceEpsg), CRS.decode(targetEpsg), true)
                point = JTS.transform(point, transform) as Point
            } catch (_: Exception) {
                data.addWarning(ErreurImportPei.ERR_EPSG_NORMALISATION.libelleLong)
            }
        }

        return point
    }

    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (userInfo.isSuperAdmin) return

        data.currentPeiId?.let { peiId ->
            data.currentPeiData = peiRepository.getInfoPei(peiId)
            val newPoint = createPeiPoint(data, appSettings.srid)

            try {
                checkZoneCompetenceContainsUseCase.checkContains(userInfo, listOf(newPoint))
            } catch (_: Exception) {
                data.addWarning(ErreurImportPei.ERR_PEI_ZONE_COMPETENCE.libelleLong)
            }
        }
    }
}
