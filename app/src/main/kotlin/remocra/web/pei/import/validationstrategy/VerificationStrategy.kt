package remocra.web.pei.import.validationstrategy

import remocra.auth.WrappedUserInfo
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

interface VerificationStrategy {
    fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo)
}
