package remocra.usecase.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException

class CreatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.INSERT) {

    override fun executeSpecific(userInfo: UserInfo?, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PEI_C)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_C)
        }
    }

    override fun checkContraintes(userInfo: UserInfo?, element: PeiData) {
        super.checkContraintes(userInfo, element)

        // TODO Vérifier zone de compétence
    }
}
