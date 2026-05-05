package remocra.web.pei.import.validationstrategy

import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.enums.ErreurImportPei
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.usecase.pei.AbstractCUDPeiUseCase
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

class PeiComputeVerificationStrategy : VerificationStrategy, AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (!userInfo.isSuperAdmin) {
            data.currentPeiData?.let { peiData ->
                if (needComputeNumero(peiData)) {
                    data.addInfos(ErreurImportPei.ERR_COMPUTE_NUMERO.libelleLong)
                }
            }
        }
    }

    override fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData) { }

    override fun checkDroits(userInfo: WrappedUserInfo) { }
}
