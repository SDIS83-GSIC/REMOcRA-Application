package remocra.usecase.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    override fun executeSpecific(userInfo: UserInfo?, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PEI_U) ||
            !userInfo.droits.contains(Droit.PEI_CARACTERISTIQUES_U) ||
            !userInfo.droits.contains(Droit.PEI_NUMERO_INTERNE_U) ||
            !userInfo.droits.contains(Droit.PEI_DEPLACEMENT_U)
        ) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }
    }
    override fun checkContraintes(userInfo: UserInfo?, element: PeiData) {
        super.checkContraintes(userInfo, element)

        // TODO vérifier que la géométrie est bien dans la zone de compétence de l'utilisateur connecté
    }
}
