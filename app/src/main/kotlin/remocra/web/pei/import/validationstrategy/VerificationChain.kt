package remocra.web.pei.import.validationstrategy

import jakarta.inject.Inject
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.db.PeiRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.zoneintegration.CheckZoneCompetenceContainsUseCase
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

class VerificationChain @Inject constructor(
    private val peiRepository: PeiRepository,
    private val appSettings: AppSettings,
    private val checkZoneCompetenceContainsUseCase: CheckZoneCompetenceContainsUseCase,
) {
    private val strategies: List<VerificationStrategy> =
        listOf(
            DateVerificationStrategy(),
            CoordinatesVerificationStrategy(),
            PeiExistVerificationStrategy(peiRepository),
            PeiZoneVerificationStrategy(peiRepository, appSettings, checkZoneCompetenceContainsUseCase),
            EpsgVerificationStrategy(),
            PeiComputeVerificationStrategy(),
        )

    fun execute(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        strategies.forEach { strategy ->
            try {
                strategy.validate(row, data, userInfo)
            } catch (e: RemocraResponseException) {
                data.addWarning(e.message)
            }
        }
    }
}
