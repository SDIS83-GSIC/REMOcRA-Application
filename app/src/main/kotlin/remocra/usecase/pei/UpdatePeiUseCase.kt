package remocra.usecase.pei

import remocra.auth.UserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.TransactionManager
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.exception.RemocraResponseException
import java.util.UUID

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    override fun executeSpecific(userInfo: UserInfo?, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PEI_U) &&
            !userInfo.droits.contains(Droit.PEI_CARACTERISTIQUES_U) &&
            !userInfo.droits.contains(Droit.PEI_NUMERO_INTERNE_U) &&
            !userInfo.droits.contains(Droit.PEI_DEPLACEMENT_U) &&
            !userInfo.droits.contains(Droit.PEI_ADRESSE_C)
        ) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }
    }

    fun updatePeiWithId(peiId: UUID, userInfo: UserInfo?, transactionManager: TransactionManager) {
        val typePei = peiRepository.getTypePei(peiId)
        val peiData =
            if (TypePei.PIBI == typePei) {
                pibiRepository.getInfoPibi(peiId)
            } else {
                penaRepository.getInfoPena(
                    peiId,
                )
            }

        this.execute(
            userInfo = userInfo,
            element = peiData,
            mainTransactionManager = transactionManager,
        )
    }
}
