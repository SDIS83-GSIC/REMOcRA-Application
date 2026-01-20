package remocra.web.pei.import.validationstrategy

import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData

// Vérification des coordonnées
class CoordinatesVerificationStrategy : VerificationStrategy {
    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (row.x == null || row.y == null) {
            throw RemocraResponseException(ErrorType.ERR_COORD_MANQUANTE)
        } else if (row.x == 0.0 || row.y == 0.0) {
            throw RemocraResponseException(ErrorType.ERR_COORD_GPS)
        } else {
            data.coordonneeX = row.x
            data.coordonneeY = row.y
        }
    }
}
