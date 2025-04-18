package remocra.usecase.commune

import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.CommuneRepository
import remocra.usecase.AbstractUseCase

class CommuneUseCase : AbstractUseCase() {
    @Inject
    lateinit var communeRepository: CommuneRepository

    fun getCommuneForSelect(userInfo: UserInfo?): List<IdCodeLibelleData> {
        if (userInfo == null) {
            throw ForbiddenException()
        }

        return if (userInfo.isSuperAdmin) {
            communeRepository.getCommuneForSelect()
        } else {
            communeRepository.getCommuneForSelectWithZone(userInfo.zoneCompetence!!.zoneIntegrationId)
        }
    }
}
