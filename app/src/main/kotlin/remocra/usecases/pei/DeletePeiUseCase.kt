package remocra.usecases.pei

import remocra.authn.UserInfo
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.Pei

class DeletePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.DELETE) {
    override fun executeSpecific(element: Pei): Any? {
        TODO("Not yet implemented")
    }

    override fun checkDroits(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }
    override fun checkContraintes(element: Pei) {
        TODO("Not yet implemented")
    }
}
