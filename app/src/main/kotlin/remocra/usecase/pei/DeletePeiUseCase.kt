package remocra.usecase.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.db.jooq.historique.enums.TypeOperation

class DeletePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.DELETE) {
    override fun executeSpecific(userInfo: UserInfo?, element: PeiData): Any? {
        TODO("Not yet implemented")
    }

    override fun checkDroits(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }
    override fun checkContraintes(userInfo: UserInfo?, element: PeiData) {
        TODO("Not yet implemented")
    }
}
