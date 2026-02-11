package remocra.apimobile.usecase.synchropeideplacement

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import org.slf4j.LoggerFactory
import remocra.apimobile.data.PeiDeplacementApiMobileData
import remocra.apimobile.repository.IncomingRepository
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class SynchroPeiDeplacementUseCase @Inject constructor(
    private val incomingRepository: IncomingRepository,
    private val peiRepository: PeiRepository,
    private val appSettings: AppSettings,
) : AbstractCUDUseCase<PeiDeplacementApiMobileData>(TypeOperation.INSERT) {

    private val logger = LoggerFactory.getLogger(SynchroPeiDeplacementUseCase::class.java)

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(Droit.MOBILE_DEPLACER_PEI_U)) {
            throw RemocraResponseException(ErrorType.API_SYNCHRO_DEPLACER_PEI_FORBIDDEN)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: PeiDeplacementApiMobileData,
    ) {
        // On ne check pas ici la zone de compétence pour ne pas bloquer la synchro, par contre au remplissage de incoming vers REMOcRA
        // si la nouvelle géométrie n'est pas dans la zone de compétence, on écrira un log dans la tâche
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: PeiDeplacementApiMobileData,
    ): PeiDeplacementApiMobileData {
        // On vérifie que le PEI existe toujours, sinon on ignore avec un log d'erreur
        if (!peiRepository.checkExists(element.peiId)) {
            logger.error("Le PEI ${element.peiId} n'existe pas dans le schéma incoming, impossible de le déplacer")
            return element
        }

        val wkt = "POINT(${element.lon} ${element.lat})"
        val fromText = WKTReader()
        val geometriePei: Geometry = fromText.read(wkt)

        val result = incomingRepository.insertPeiDeplacement(
            peiId = element.peiId,
            peiDeplacementGeometry = geometriePei,
            srid = appSettings.srid,
            tourneeId = element.tourneeId,
        )

        when (result) {
            0 -> {
                logger.warn("Le PEI ${element.peiId} est déjà dans le schéma incoming")
            }

            1 -> Unit // OK
            else -> {
                logger.error("Erreur à l'insertion du déplacement du PEI ${element.peiId} dans le schéma incoming, résultat : $result")
            }
        }

        return element
    }

    override fun postEvent(
        element: PeiDeplacementApiMobileData,
        userInfo: WrappedUserInfo,
    ) {
        // On ne poste pas d'évènement comme c'est une insertion dans le schéma incoming
    }
}
