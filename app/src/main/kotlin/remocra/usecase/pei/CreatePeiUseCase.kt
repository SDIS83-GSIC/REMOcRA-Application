package remocra.usecase.pei

import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException

class CreatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.INSERT) {

    override fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroits(droitsWeb = setOf(Droit.PEI_C))) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_C)
        }
    }
}
