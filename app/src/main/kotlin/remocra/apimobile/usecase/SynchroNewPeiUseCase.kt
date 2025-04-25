package remocra.apimobile.usecase

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKTReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.apimobile.data.NewPeiForMobileApiData
import remocra.apimobile.repository.IncomingRepository
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class SynchroNewPeiUseCase : AbstractCUDUseCase<NewPeiForMobileApiData>(TypeOperation.INSERT) {

    @Inject
    private lateinit var incomingRepository: IncomingRepository

    @Inject
    private lateinit var appSettings: AppSettings

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SynchroNewPeiUseCase::class.java)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.MOBILE_PEI_C)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_PEI_FORBIDDEN)
        }
    }

    override fun postEvent(element: NewPeiForMobileApiData, userInfo: WrappedUserInfo) {
        // On n'insère pas d'évènement dans la traçabilité parce qu'on insère dans incoming
    }

    override fun execute(userInfo: WrappedUserInfo, element: NewPeiForMobileApiData): NewPeiForMobileApiData {
        // On va chercher toutes les infos dont on a besoin

        // Géométrie du PEI
        val geometriePei: Geometry = getGeometrieWithCoordonnnees(element.lon, element.lat)

        // Commune
        val communeId: UUID? = incomingRepository.getCommuneWithGeometrie(geometriePei, appSettings.srid)

        val peiVoieId = incomingRepository.getVoie(geometriePei, appSettings.srid)

        if (communeId == null) {
            logger.error("Impossible d'insérer le PEI : il n'est sur aucune commune connue.")
            throw RemocraResponseException(ErrorType.API_SYNCHRO_NO_COMMUNE)
        }

        val result: Int =
            incomingRepository.insertPei(
                element.peiId,
                element.gestionnaireId,
                communeId,
                element.natureId,
                element.natureDeciId,
                element.peiTypePei,
                element.peiObservation,
                geometriePei,
                peiVoieId,
                appSettings.srid,
            )
        when (result) {
            0 -> {
                logger.warn("Le PEI ${element.peiId} est déjà dans le schéma incoming")
            }

            1 -> Unit // OK
            else -> {
                logger.error("Impossible d'insérer le PEI ${element.peiId} dans le schéma incoming")
                throw RemocraResponseException(ErrorType.API_SYNCHRO_PEI_ERROR, element.peiId.toString())
            }
        }
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: NewPeiForMobileApiData) {
        // On check la commune dans le exécute puisqu'on a besoin de l'id
    }

    /**
     * Permet de récupérer la géométrie du point d'eau
     *
     * @param longitude
     * @param latitude
     * @return
     */
    fun getGeometrieWithCoordonnnees(longitude: Double, latitude: Double): Geometry {
        val wkt = "POINT($longitude $latitude)"
        val fromText = WKTReader()
        var geometry: Geometry
        try {
            geometry = fromText.read(wkt)
            geometry.setSRID(appSettings.srid)
        } catch (e: ParseException) {
            throw RuntimeException("Impossible de trouver la géométrie du PEI :$wkt")
        }
        return geometry
    }
}
