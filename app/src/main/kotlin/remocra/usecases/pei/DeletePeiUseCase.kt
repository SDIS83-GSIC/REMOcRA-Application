package remocra.usecases.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.db.jooq.historique.enums.TypeOperation

class DeletePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.DELETE) {
    override fun executeSpecific(element: PeiData): Any? {
        TODO("Not yet implemented")
    }

    override fun checkDroits(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }
    override fun checkContraintes(userInfo: UserInfo?, element: PeiData) {
        TODO("Not yet implemented")
    }
}
