package remocra.usecase.commune

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.GlobalData
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository
import remocra.usecase.AbstractUseCase

class CommuneUseCase
@Inject
constructor(
    private val communeRepository: CommuneRepository,
) :
    AbstractUseCase() {

    fun getCommuneForSelect(userInfo: WrappedUserInfo): List<IdCodeLibelleData> {
        return if (userInfo.isSuperAdmin) {
            communeRepository.getCommuneForSelect()
        } else {
            communeRepository.getCommuneForSelectWithZone(userInfo.zoneCompetence!!.zoneIntegrationId)
        }
    }

    fun getCommuneByName(libelle: String): List<GlobalData.ItemSearch> {
        return communeRepository.getCommuneByName(libelle)
    }
}
