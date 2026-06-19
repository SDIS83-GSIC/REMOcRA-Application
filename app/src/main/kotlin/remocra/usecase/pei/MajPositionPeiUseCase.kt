package remocra.usecase.pei

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import remocra.GlobalConstants
import remocra.apimobile.usecase.GetCommuneVoieByGeomUseCase
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.ErrorType
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.web.pei.import.ImportPeiData

class MajPositionPeiUseCase @Inject constructor(
    private val peiUseCase: PeiUseCase,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val getCommuneVoieByGeomUseCase: GetCommuneVoieByGeomUseCase,
    private val appSettings: AppSettings,
    private val parametresProvider: ParametresProvider,
    private val transactionManager: TransactionManager,
) : AbstractUseCase() {

    fun execute(importPeiData: ImportPeiData, userInfo: WrappedUserInfo): Result {
        if (!userInfo.hasDroits(droitWeb = Droit.PEI_DEPLACEMENT_U)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }

        val voieSaisieLibre = parametresProvider.getParametreBoolean(GlobalConstants.VOIE_SAISIE_LIBRE)
            ?: throw IllegalArgumentException("Le paramètre VOIE_SAISIE_LIBRE est nul, veuillez renseigner une valeur")

        val toleranceVoie = parametresProvider.getParametreInt(GlobalConstants.TOLERANCE_VOIES_METRES)
            ?: throw IllegalArgumentException("TOLERANCE_VOIES_METRES nul")

        val peiToUpdate = importPeiData.bilanVerifications.mapNotNull { ligne ->
            val peiId = ligne.currentPeiId ?: return@mapNotNull null
            val geom = GeometryFactory(PrecisionModel(), appSettings.srid).createPoint(
                Coordinate(ligne.coordonneeX, ligne.coordonneeY),
            )

            val locationData = getCommuneVoieByGeomUseCase.execute(geom, toleranceVoie)
            val currentPei = peiUseCase.getInfoPei(peiId)

            var voieTexte = currentPei.peiVoieTexte
            if (locationData.voieId == null && (currentPei.peiVoieTexte == null || !voieSaisieLibre)) {
                throw RemocraResponseException(
                    ErrorType.ERR_PEI_VOIE,
                    "Le PEI ${currentPei.peiId} ne peut pas être déplacé car il n'y a pas de voie trouvée après son déplacement.",
                )
            } else if (locationData.voieId != null && currentPei.peiVoieTexte != null) {
                voieTexte = null
            }

            when (currentPei) {
                is PenaData -> currentPei.copy(
                    peiGeometrie = geom,
                    peiCommuneId = locationData.communeIdApresDeplacement
                        ?: throw RemocraResponseException(ErrorType.COMMUNE_NOT_FOUND),
                    peiVoieId = locationData.voieId,
                    peiVoieTexte = voieTexte,
                    peiObservation = ligne.observation,
                )
                is PibiData -> currentPei.copy(
                    peiGeometrie = geom,
                    peiCommuneId = locationData.communeIdApresDeplacement
                        ?: throw RemocraResponseException(ErrorType.COMMUNE_NOT_FOUND),
                    peiVoieId = locationData.voieId,
                    peiVoieTexte = voieTexte,
                    peiObservation = ligne.observation,
                )
                else -> throw RemocraResponseException(ErrorType.ERR_PEI_TYPE)
            }
        }

        if (peiToUpdate.isNotEmpty()) {
            return transactionManager.transactionResult {
                peiToUpdate.map { pei ->
                    val result = updatePeiUseCase.execute(userInfo, pei, transactionManager)
                    if (result !is Result.Success) {
                        return@transactionResult result
                    }
                }
                return@transactionResult Result.Success()
            }
        }
        return Result.Success()
    }
}
