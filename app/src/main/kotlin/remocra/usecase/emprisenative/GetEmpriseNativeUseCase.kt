package remocra.usecase.emprisenative

import jakarta.inject.Inject
import org.geotools.referencing.CRS
import org.slf4j.LoggerFactory
import remocra.GlobalConstants.SRID_4326
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.data.enums.ParametreEnum
import remocra.db.ZoneIntegrationRepository
import remocra.usecase.AbstractUseCase

class GetEmpriseNativeUseCase @Inject constructor(
    val appSettings: AppSettings,
    val parametresProvider: ParametresProvider,
    private val zoneIntegrationRepository: ZoneIntegrationRepository,

) : AbstractUseCase() {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(): EmpriseParDefaut {
        val geometrie = parametresProvider.getParametreString(ParametreEnum.EMPRISE_NATIVE.name)
            ?.let { zoneIntegrationRepository.getGeometrieByCode(it) }

        val (srid, extent) = if (geometrie != null && geometrie.envelopeInternal != null && geometrie.srid != 0) {
            val env = geometrie.envelopeInternal
            geometrie.srid to listOf(env.minX, env.minY, env.maxX, env.maxY)
        } else {
            if (geometrie?.srid == 0) {
                logger.error(
                    "Le SRID de la geometrie " +
                        "${parametresProvider.getParametreString(ParametreEnum.EMPRISE_NATIVE.name)}" +
                        " n'est pas défini",
                )
            }
            val bbox = CRS.getGeographicBoundingBox(CRS.decode(appSettings.epsg.name))
            SRID_4326 to listOf(
                bbox.westBoundLongitude,
                bbox.southBoundLatitude,
                bbox.eastBoundLongitude,
                bbox.northBoundLatitude,
            )
        }

        return EmpriseParDefaut(
            name = appSettings.epsg.name,
            projection = appSettings.epsg.projection,
            extent = extent,
            extentSRID = "EPSG:$srid",
        )
    }

    data class EmpriseParDefaut(
        val name: String,
        val projection: String,
        val extent: Collection<Double>,
        val extentSRID: String,
    )
}
