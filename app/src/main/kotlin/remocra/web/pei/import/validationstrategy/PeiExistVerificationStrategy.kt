package remocra.web.pei.import.validationstrategy

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErreurImportPei
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.exception.RemocraResponseException
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

class PeiExistVerificationStrategy @Inject constructor(
    private val peiRepository: PeiRepository,
) : VerificationStrategy {

    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (row.numero.isNullOrBlank()) {
            throw RemocraResponseException(ErrorType.ERR_PEI_MANQUANT)
        }

        val currentPeiId = peiRepository.getPeiIdFromNumero(row.numero)

        if (currentPeiId == null) {
            data.addWarning(ErreurImportPei.ERR_PEI_INEXISTANT.libelleLong)
        } else {
            data.currentPeiId = currentPeiId
        }
    }
}
