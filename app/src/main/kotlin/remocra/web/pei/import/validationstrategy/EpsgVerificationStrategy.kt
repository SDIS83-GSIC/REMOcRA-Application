package remocra.web.pei.import.validationstrategy

import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

class EpsgVerificationStrategy : VerificationStrategy {
    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (row.epsg.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.ERR_EPSG_MANQUANT)
        } else
            data.epsg = row.epsg
    }
}
