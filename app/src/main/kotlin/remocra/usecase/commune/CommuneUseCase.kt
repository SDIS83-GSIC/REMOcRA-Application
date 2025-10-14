package remocra.usecase.commune

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository
import remocra.usecase.AbstractUseCase

class CommuneUseCase : AbstractUseCase() {
    @Inject
    lateinit var communeRepository: CommuneRepository

    fun getCommuneForSelect(userInfo: WrappedUserInfo): List<IdCodeLibelleData> {
        return if (userInfo.isSuperAdmin) {
            communeRepository.getCommuneForSelect()
        } else {
            communeRepository.getCommuneForSelectWithZone(userInfo.zoneCompetence!!.zoneIntegrationId)
        }
    }
}
