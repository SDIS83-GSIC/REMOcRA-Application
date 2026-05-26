package remocra.usecase.pei

import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException

class CreatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.INSERT) {

    override fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if ((!userInfo.hasDroit(droitWeb = Droit.PEI_C) && userInfo.typeSourceModification == TypeSourceModification.REMOCRA_WEB) ||
            (!userInfo.hasDroits(droitWeb = Droit.MOBILE_PEI_C) && userInfo.typeSourceModification == TypeSourceModification.MOBILE)
        ) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_C)
        }
    }
}
