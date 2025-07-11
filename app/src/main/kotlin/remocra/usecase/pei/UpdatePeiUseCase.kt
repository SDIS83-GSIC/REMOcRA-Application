package remocra.usecase.pei

import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.enums.TypePei
import remocra.exception.RemocraResponseException
import java.util.UUID

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    override fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData) {
        upsertPei(element)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroits(
                droitsWeb = setOf(Droit.PEI_U, Droit.PEI_CARACTERISTIQUES_U, Droit.PEI_NUMERO_INTERNE_U, Droit.PEI_DEPLACEMENT_U, Droit.PEI_ADRESSE_C),
                droitsApi = setOf(DroitApi.ADMINISTRER, DroitApi.TRANSMETTRE),
            )
        ) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }
    }

    fun updatePeiWithId(peiId: UUID, userInfo: WrappedUserInfo) {
        val typePei = peiRepository.getTypePei(peiId)
        val peiData =
            if (TypePei.PIBI == typePei) {
                pibiRepository.getInfoPibi(peiId)
            } else {
                penaRepository.getInfoPena(peiId)
            }

        execute(
            userInfo = userInfo,
            element = peiData,
        )
    }
}
