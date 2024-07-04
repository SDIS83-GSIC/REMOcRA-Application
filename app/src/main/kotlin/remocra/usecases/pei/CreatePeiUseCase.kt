package remocra.usecases.pei

import remocra.authn.UserInfo
import remocra.data.PeiData
import remocra.db.jooq.historique.enums.TypeOperation

class CreatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.INSERT) {
    override fun executeSpecific(element: PeiData): Any? {
        TODO("Not yet implemented")
    }

    override fun checkDroits(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }

    override fun checkContraintes(element: PeiData) {
        TODO("Not yet implemented")
    }
}
