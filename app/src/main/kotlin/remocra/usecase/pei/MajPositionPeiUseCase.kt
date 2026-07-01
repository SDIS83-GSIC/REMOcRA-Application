package remocra.usecase.pei

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.ErrorType
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.web.pei.import.ImportPeiData
import remocra.web.pei.import.LigneImportPeiData

class MajPositionPeiUseCase @Inject constructor(
    private val peiUseCase: PeiUseCase,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val appSettings: AppSettings,
) : AbstractUseCase() {

    fun execute(importPeiData: ImportPeiData, userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroits(droitWeb = Droit.PEI_DEPLACEMENT_U)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }

        importPeiData.bilanVerifications.forEach {
            modificationPositionPei(it, userInfo)
        }
    }

    private fun modificationPositionPei(dataImport: LigneImportPeiData, userInfo: WrappedUserInfo) {
        val newPoint = GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(
            Coordinate(
                dataImport.coordonneeX,
                dataImport.coordonneeY,
            ),
        )

        dataImport.currentPeiId?.let { peiId ->
            val data = peiUseCase.getInfoPei(peiId)
            val finalDate = dataImport.currentDate?.let { dateUtils.getMoment(it) }

            val newData: PeiData = when (data) {
                is PenaData -> data.copy(peiGeometrie = newPoint, peiObservation = dataImport.observation, peiDateReleveGps = finalDate)
                is PibiData -> data.copy(peiGeometrie = newPoint, peiObservation = dataImport.observation, peiDateReleveGps = finalDate)
                else -> throw RemocraResponseException(ErrorType.ERR_PEI_TYPE)
            }

            updatePeiUseCase.execute(userInfo, newData)
        }
    }
}
